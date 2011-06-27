package org.openqa.safari.Servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.BlockingQueue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
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
		System.out.println("extension calling");
		if (req.getPathInfo().contains("result")) {
			JSONObject r = extractBody(req);
			safari.updateResponse(r);
		} else {
			BlockingQueue<WebDriverCommand> queue = safari.getCommandQueue();

			try {
				safari.setReady(true);
				System.out.println("blocked waiting for a command");
				WebDriverCommand command = queue.take();
				System.out.println("sending command " + command);
				OutputStream out = resp.getOutputStream();
				out.write(command.toJSON().toString().getBytes("UTF-8"));
			} catch (InterruptedException e) {
				System.err.println("kicked out");
			}
		}

	}

	private static JSONObject extractBody(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			InputStream is = request.getInputStream();
			if (is == null) {
				return null;
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			while ((line = reader.readLine()) != null) {
				sb.append(line);/* .append("\n"); */
			}
			is.close();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		String body = sb.toString();
		if ("".equals(body)) {
			return null;
		}
		try {
			System.out.println("got "+body+" from the ext.");
			return new JSONObject(body);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

}
