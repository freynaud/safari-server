var logAll = true;

function newPageEventLoading() {
	if (window.top === window) {
		var page = new Object();
		page.loading = true;
		page.script = "start";
		safari.self.tab.dispatchMessage("loading", page);
	}
}

newPageEventLoading();

function log(o) {
	if (logAll) {
		console.log(o);
	}
}