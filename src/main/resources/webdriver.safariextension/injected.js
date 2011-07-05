var logAll = true;

Injected = function() {
	// page unique ID
	this.id = null;
	// id for the element cache
	this.currentId = 0;

	this.cache = new Object();

	this.methods = {};

};

Injected.prototype.init = function() {
	this.methods["POST /session/:sessionId/element"] = this.findElement;
	this.methods["POST /session/:sessionId/elements"] = this.findElements;
	this.methods["POST /session/:sessionId/element/:id/value"] = this.sendKeys;
	this.methods["POST /session/:sessionId/element/:id/click"] = this.click;
	this.id = injected.UUID();
	safari.self.addEventListener("message", injected.handleMessage, false);

};

Injected.prototype.UUID = function() {
	log("uuid");
	var S4 = function() {
		return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
	};
	
	return (S4() + S4() + "-" + S4() + "-" + S4() + "-" + S4() + "-" + S4() + S4() + S4());
};

Injected.prototype.handleMessage = function(event) {
	var name = event.name;

	if (name === "command") {
		if (injected.isSelectedPage()) {
			var json = event.message;
			var method = json.method;
			var path = json.path;
			var genericPath = json.genericPath;
			var content = json.content;
			var version = json.version;
			var result;

			var m = injected.methods[json.method + " " + json.genericPath]
			if (m) {
				result = m(json);
			} else {
				result = createResult();
				result.status = 13;
				result.value.message = "INJECTED : " + method + " : "
						+ genericPath + " is not implemented in injected.js";
			}
			safari.self.tab.dispatchMessage("result", result);
		}
	} else if (name === "ping") {
		var requestScriptId = event.message;
		log("got ping on " + requestScriptId + " , I am " + id);
		if (id === requestScriptId) {
			safari.self.tab.dispatchMessage("pong", document.readyState);
		}
	}
};

Injected.prototype.click = function(json) {
	var version = json.version;
	var content = json.content;

	var result = createResult();

	var internalId = content.id;
	var element = injected.cache[internalId];
	var evt = document.createEvent("MouseEvents");
	evt.initMouseEvent("click", true, true, window, 0, 0, 0, 0, 0, false,
			false, false, false, 0, null);
	element.dispatchEvent(evt);
	result.change = true;
	result.version = version;
	return result;
};

Injected.prototype.sendKeys = function(json) {
	var version = json.version;
	var content = json.content;

	var internalId = content.id;
	var value = content.value;
	var element = injected.cache[internalId];
	var result = createResult();
	if (element) {
		element.focus();
		log(value + " - " + value.length);
		for (i = 0; i < value.length; i++) {
			var c = value[i];
			injected.keyDown(c, element);
			// TODO freynaud why ?
			element.value = element.value + c;
			injected.keyUp(c, element);
		}
	} else {
		result.state = "10";
		result.value.message = "element not in the cache";
	}
	result.version = version;
	return result;
};

Injected.prototype.keyUp = function(key, el) {
	var evt = document.createEvent("KeyboardEvent");
	evt.initKeyboardEvent('keyup', false, true, null, false, false, false,
			false, 0, key);
	el.dispatchEvent(evt);

};

Injected.prototype.keyDown = function(key, el) {
	var evt = document.createEvent("KeyboardEvent");
	evt.initKeyboardEvent('keydown', false, true, null, false, false, false,
			false, 0, key);
	el.dispatchEvent(evt);
};

Injected.prototype.findElement = function(json) {
	var version = json.version;
	var content = json.content;

	log("find element");
	var using = content.using;
	var value = content.value;
	log('by ' + using + " = " + value);
	var result = createResult();
	log(result);
	var el;
	if (using === "id") {
		el = document.getElementById(value);
	} else if (using === "name") {
		var els = document.getElementsByName(value);
		if (els.length > 0) {
			el = els[0];
		}
	} else {
		log("not supported strategy");
		result.status = 13;
		result.value.message = "finding strategy not supported " + using;
		return result;
	}

	log('found ' + el);
	if (el) {
		var id = injected.generateId();
		log('new element found ' + id + ' using strategy ' + using
				+ ', value =' + value);
		result.value['ELEMENT'] = id;
		injected.cache[id] = el;
	} else {
		result.status = 7;
		result.value.message = "page loaded:" + document.readyState
				+ "couldn't find the element using strategy By." + using + "="
				+ value;
	}
	return result;
};

Injected.prototype.generateId = function(json) {
	this.currentId = this.currentId + 1;
	return this.currentId;
};

function inspect() {
	console.log('inspecting ' + document.location.href);
	var iframes = document.getElementsByTagName('iframe');
	var divs = document.getElementsByTagName('div');

	console.log('found ' + iframes.length + ' iframes');
	console.log('found ' + divs.length + ' divs');
	for (i = 0; i < iframes.length; i++) {
		console.log('found iframe ' + document.location.href + " ,src="
				+ iframes[i].src);
	}
};

function createResult() {
	var result = new Object();
	result.status = 0;
	result.id = injected.id;
	result.session = "TODO in global";
	result.value = new Object();
	return result;
};

function assignPageId() {
	if (window.top === window) {
		var id = guidGenerator();
		// log("id for page " + id);
		// log("title: "+window.document.title);
		// log("url: "+window.document.URL);
	} else {
		var id = guidGenerator();
		// log("id for iframe " + id);
		// log("title: "+window.document.title);
		// log("url: "+window.document.URL);
	}
};
Injected.prototype.sendNewPageLoadedEvent = function() {
	if (window.top === window) {
		var page = new Object();
		page.loading = false;
		page.script = "end";
		page.id = this.id;
		safari.self.tab.dispatchMessage("loading", page);
		log(this.id);
	}
};
Injected.prototype.isSelectedPage = function() {
	return window.top === window;
};

var injected = new Injected();
injected.init();
injected.sendNewPageLoadedEvent();

function log(o) {
	if (logAll) {
		console.log(o);
	}
};