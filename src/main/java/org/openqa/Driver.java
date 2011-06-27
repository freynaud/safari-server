package org.openqa;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import org.openqa.jetty.http.SocketListener;
import org.openqa.jetty.jetty.Server;
import org.openqa.jetty.jetty.servlet.WebApplicationContext;
import org.openqa.safari.SafariProxy;
import org.openqa.safari.Servlets.CommandServlet;
import org.openqa.safari.Servlets.SafariExtensionInit;
import org.openqa.safari.Servlets.SafariExtensionProxy;

public class Driver {

	
	public static final Map<String, SafariProxy> safaris = new ConcurrentHashMap<String, SafariProxy>();
	
	
	public static void main(String[] args) throws Exception {
		Server server = new Server();
		SocketListener socketListener = new SocketListener();
		socketListener.setMaxIdleTimeMs(60000);
		socketListener.setPort(9999);
		server.addListener(socketListener);

		WebApplicationContext root = server.addWebApplication("", ".");

		
		root.addServlet("/safari-init/*", SafariExtensionInit.class.getName());
		root.addServlet("/safari-extension/*", SafariExtensionProxy.class.getName());
		root.addServlet("/wd/hub/*", CommandServlet.class.getName());
		
		server.start();
	}
}
