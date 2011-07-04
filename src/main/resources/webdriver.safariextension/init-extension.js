function init() {
	var session = getSession();
	if (session) {
		var settings = new Object();
		settings.session = getSession();
		safari.self.tab.dispatchMessage("init", settings);
	}

}

function getSession() {
	var session = document.getElementById('session');
	if (session) {
		var s = session.innerHTML;
		return s;
	}
}
init();

