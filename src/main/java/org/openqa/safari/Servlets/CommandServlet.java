package org.openqa.safari.Servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.crypto.RuntimeCryptoException;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.Driver;
import org.openqa.WebDriverCommand;
import org.openqa.safari.CommandHandlerFactory;
import org.openqa.safari.NewSessionCommandHandler;
import org.openqa.safari.SafariProxy;
import org.openqa.safari.Utils;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * recieves the command from the test/
 * 
 * @author freynaud
 * 
 */
public class CommandServlet extends HttpServlet {

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

	private CommandHandlerFactory factory = new CommandHandlerFactory();

	private void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		WebDriverCommand command = new WebDriverCommand();
		command.setMethod(request.getMethod());
		command.setPath(request.getPathInfo());
		command.setContent(extractBody(request));
		System.out.println("got a " + request.getMethod() + " on " + request.getPathInfo()+" : "+command.getContent());
		response.setContentType("application/json");

		SafariProxy safari = null;
		if (command.isNewSession()) {
			safari = new SafariProxy();
			String session = safari.getSession();
			Driver.safaris.put(session, safari);
			safari.launch();
			response.addHeader("location", request.getServletPath() + "/session/" + session);
			response.setStatus(303);
			safari.noResponseFromExtensionExpected();
		} else if (command.isDeleteSession()) {
			String session = Utils.extractSessionFromPath(request.getPathInfo());
			safari = Driver.safaris.get(session);
			safari.noResponseFromExtensionExpected();
			safari.quit();
		} else if (command.isGetSession()) {
			String session = Utils.extractSessionFromPath(request.getPathInfo());
			safari = Driver.safaris.get(session);
			JSONObject cap = new JSONObject(new DesiredCapabilities("safari", "5.0", Platform.getCurrent()).asMap());
			safari.noResponseFromExtensionExpected();
			write(command.getSession(), 0, cap, response);
		} else {
			String session = Utils.extractSessionFromPath(request.getPathInfo());
			safari = Driver.safaris.get(session);
			safari.addCommand(command);
			System.out.println("Cammand added to the queue.");
		}

		System.out.println(safari.getResponse());

	}

	private void write(String session, int status, JSONObject value, HttpServletResponse resp) {
		try {
			JSONObject res = new JSONObject();
			res.put("sessionId", session);
			res.put("status", status);
			res.put("value", value);
			OutputStream out = resp.getOutputStream();
			out.write(res.toString().getBytes("UTF-8"));
		} catch (Exception e) {
			throw new RuntimeException(e);
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
			return new JSONObject(body);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

}
