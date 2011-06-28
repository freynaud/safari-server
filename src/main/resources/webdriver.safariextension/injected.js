safari.self.addEventListener("message", handleMessage, false);
var logAll = true;
var cache = new Object();

function handleMessage(event) {
	log('injected script got event ' + event.name + " , with content=" + event.message);

	var name = event.name;

	if (name === "command") {

		if (isSelectedPage()) {
			var json = event.message;

			var method = json.method;
			var path = json.path;
			var genericPath = json.genericPath;
			var content = json.content;
			var version = json.version;
			var result;

			if (method === "GET" && genericPath === "/session/:sessionId/title") {
				result = getTitle();
			} else if (method === "POST" && genericPath === "/session/:sessionId/element") {
				result = findElement(content);
			} else if (method === "POST" && genericPath === "/session/:sessionId/element/:id/value") {
				result = sendKeys(content,version);
			} else if (method === "POST" && genericPath === "/session/:sessionId/element/:id/click") {
				result = click(content,version);
			} else {

				result = result();
				result.status = 13;
				result.value.message = "INJECTED : " + method + " : " + genericPath + " is not implemented in injected.js";
			}
			safari.self.tab.dispatchMessage("result", result);
		}
	}
}

function click(content,version) {
	var result = createResult();

	var internalId = content.id;
	var element = cache[internalId];
	var evt = document.createEvent("MouseEvents");
	evt.initMouseEvent("click", true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
	element.dispatchEvent(evt);
	result.change = true;
	result.version = version;
	return result;
}

function sendKeys(content,version) {
	var internalId = content.id;
	var value = content.value;
	var element = cache[internalId];
	var result = createResult();
	if (element) {
		element.focus();
		log(value + " - " + value.length);
		for (i = 0; i < value.length; i++) {
			var c = value[i];
			keyDown(c, element);
			// TODO freynaud why ?
			element.value = element.value + c;
			keyUp(c, element);
		}
	} else {
		result.state = "10";
		result.value.message = "element not in the cache";
	}
	result.version = version;
	return result;
}

function keyUp(key, el) {
	var evt = document.createEvent("KeyboardEvent");
	evt.initKeyboardEvent('keyup', false, true, null, false, false, false, false, 0, key);
	el.dispatchEvent(evt);

}
function keyDown(key, el) {
	var evt = document.createEvent("KeyboardEvent");
	evt.initKeyboardEvent('keydown', false, true, null, false, false, false, false, 0, key);
	el.dispatchEvent(evt);
}

function getTitle() {
	var title = window.document.title;
	var result = createResult();
	result.value = title;
	return result;
}
function findElement(content) {
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
		var id = generateId();
		log('new element found ' + id + ' using strategy ' + using + ', value =' + value);
		result.value['ELEMENT'] = id;
		cache[id] = el;
	} else {
		result.status = 7;
		result.value.message = "page loaded:" + document.readyState + "couldn't find the element using strategy By." + using + "=" + value;
	}
	return result;
}

var currentId = 0;
function generateId() {
	currentId++;
	return currentId;
}

function inspect() {
	console.log('inspecting ' + document.location.href);
	var iframes = document.getElementsByTagName('iframe');
	var divs = document.getElementsByTagName('div');

	console.log('found ' + iframes.length + ' iframes');
	console.log('found ' + divs.length + ' divs');
	for (i = 0; i < iframes.length; i++) {
		console.log('found iframe ' + document.location.href + " ,src=" + iframes[i].src);
	}
}

function createResult() {
	var result = new Object();
	result.status = 0;
	result.session = "TODO in global";
	result.value = new Object();
	return result;
}

function guidGenerator() {
	var S4 = function() {
		return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
	};
	return (S4() + S4() + "-" + S4() + "-" + S4() + "-" + S4() + "-" + S4() + S4() + S4());
}

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
}
function newPageLoadedEvent() {
	if (window.top === window) {
		var page = new Object();
		page.loading = false;
		page.script = "end";
		page.id = id;
		safari.self.tab.dispatchMessage("loading", page);
		log(id);
	}

}
var id = guidGenerator();
newPageLoadedEvent();

function isSelectedPage() {
	return window.top === window;
}

function log(o) {
	if (logAll) {
		console.log(o);
	}
}
