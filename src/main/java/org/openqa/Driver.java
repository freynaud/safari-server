package org.openqa;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openqa.jetty.http.SocketListener;
import org.openqa.jetty.jetty.Server;
import org.openqa.jetty.jetty.servlet.WebApplicationContext;
import org.openqa.safari.SafariProxy;
import org.openqa.safari.Servlets.CommandServlet;
import org.openqa.safari.Servlets.Misc;
import org.openqa.safari.Servlets.SafariExtensionInit;
import org.openqa.safari.Servlets.SafariExtensionProxy;

public class Driver {

	public static final Map<String, SafariProxy> safaris = new ConcurrentHashMap<String, SafariProxy>();
	private Server server = new Server();

	public Driver() {
		try {
			SocketListener socketListener = new SocketListener();
			socketListener.setMaxIdleTimeMs(60000);
			socketListener.setPort(9999);
			server.addListener(socketListener);

			WebApplicationContext root = server.addWebApplication("", ".");
			root.addServlet("/*", Misc.class.getName());
			root.addServlet("/safari-init/*", SafariExtensionInit.class.getName());
			root.addServlet("/safari-extension/*", SafariExtensionProxy.class.getName());
			root.addServlet("/wd/hub/*", CommandServlet.class.getName());
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public void start() throws Exception {
		server.start();
	}

	public void stop() throws InterruptedException {
		server.stop();
	}

	public static void main(String[] args) throws Exception {
		Driver server = new Driver();
		server.start();
	}
}
