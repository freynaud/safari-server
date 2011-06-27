package org.openqa.safari.Servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openqa.Driver;
import org.openqa.WebDriverCommand;
import org.openqa.safari.SafariProxy;
import org.openqa.safari.Utils;

/**
 * 
 * @author freynaud
 *
 */
public class SafariExtensionProxy extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	}

	private void process(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String session = Utils.extractSessionFromPath(req.getPathInfo());
		SafariProxy safari = Driver.safaris.get(session);
		
		BlockingQueue<WebDriverCommand> queue = safari.getCommandQueue();
		try {
			safari.setReady(true);
			WebDriverCommand command = queue.take();
			OutputStream out = resp.getOutputStream();
			out.write(command.toJSON().toString().getBytes("UTF-8"));
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		
		
	}
	
	
}
