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

			var result;

			if (method === "GET" && genericPath === "/session/:sessionId/title") {
				result = getTitle();
			} else if (method === "POST" && genericPath === "/session/:sessionId/element") {
				result = findElement(content);
			} else {
				result = result();
				result.status = 13;
				result.value.message = "INJECTED : " + method + " : " + genericPath + " is not implemented in injected.js";
			}
			safari.self.tab.dispatchMessage("result", result);
		}
	}
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
	log('by '+using+" = "+value);
	var result = createResult();
	log(result);
	if ( using === "id") {
		var el = document.getElementById(value);
		log('found '+el );
		if (el) {
			var id = generateId();
			log('new element found ' + id + ' using strategy ' + using + ', value =' + value);
			result.value['ELEMENT'] = id;
			cache[id] = el;
		} else {
			result.status = 7;
			result.value.message = "page loaded:"+ document.readyState+"couldn't find the element using strategy By."+using +"="+value;
		}
	} else {
		log("not supported strategy");
		result.status = 13;
		result.value.message = "finding strategy not supported " + using;
	}
	log(result);
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
		safari.self.tab.dispatchMessage("loading", false);
	}
}

newPageLoadedEvent();

function isSelectedPage() {
	return window.top === window;
}

function log(o) {
	if (logAll) {
		console.log(o);
	}
}
