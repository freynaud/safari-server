var logAll = true;

function newPageEventLoading() {
	if (window.top === window) {
		safari.self.tab.dispatchMessage("loading", true);
	}
}

newPageEventLoading();

function log(o) {
	if (logAll) {
		console.log(o);
	}
}