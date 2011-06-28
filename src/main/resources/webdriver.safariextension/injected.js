safari.self.addEventListener("message", handleMessage, false);
var logAll = true;

function handleMessage(event) {
	log('injected script got event ' + event.name + " , with content=" + event.message);

	var name = event.name;

	if (name === "command") {

		if (isSelectedPage()) {
			var json = event.message;
			alert('got command ' + name + json);
			var result = new Object();
			result.status = 13;
			result.value = new Object();
			result.value.message = "not implemented in the injected script.";

			safari.self.tab.dispatchMessage("result", result);
		}
	}
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
