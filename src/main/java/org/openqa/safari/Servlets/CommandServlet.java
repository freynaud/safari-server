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
import org.openqa.selenium.remote.JsonToBeanConverter;
import org.openqa.selenium.remote.Response;

/**
 * recieves the command from the test/
 * 
 * @author freynaud
 * 
 */
public class CommandServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			process(req, resp);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			process(req, resp);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			process(req, resp);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	private CommandHandlerFactory factory = new CommandHandlerFactory();

	private void process(HttpServletRequest request, HttpServletResponse response) throws IOException, JSONException {

		WebDriverCommand command = new WebDriverCommand();
		command.setMethod(request.getMethod());
		command.setPath(request.getPathInfo());
		command.setContent(extractBody(request));
		System.out.println("got a " + request.getMethod() + " on " + request.getPathInfo() + " : " + command.getContent());
		response.setContentType("application/json");

		SafariProxy safari = null;
		if (command.isNewSession()) {
			safari = new SafariProxy();
			String session = safari.getSession();
			Driver.safaris.put(session, safari);
			
			JsonToBeanConverter conveter = new JsonToBeanConverter();
			DesiredCapabilities requested = conveter.convert(DesiredCapabilities.class, command.getContent().get("desiredCapabilities"));
			safari.launch(requested);
			response.addHeader("location", request.getServletPath() + "/session/" + session);
			response.setStatus(303);
			JSONObject o = Utils.getResponse(session, 0, new JSONObject());
			safari.noResponseFromExtensionExpected(o);
		} else if (command.isDeleteSession()) {
			String session = Utils.extractSessionFromPath(request.getPathInfo());
			safari = Driver.safaris.get(session);
			JSONObject o = Utils.getResponse(session, 0, new JSONObject());
			safari.noResponseFromExtensionExpected(o);
			safari.quit();
		} else if (command.isGetSession()) {
			String session = Utils.extractSessionFromPath(request.getPathInfo());
			safari = Driver.safaris.get(session);
			JSONObject cap = new JSONObject(new DesiredCapabilities("safari", "5.0", Platform.getCurrent()).asMap());
			JSONObject o = Utils.getResponse(session, 0, cap);
			safari.noResponseFromExtensionExpected(o);
		} else {
			String session = Utils.extractSessionFromPath(request.getPathInfo());
			safari = Driver.safaris.get(session);
			safari.addCommand(command);
			System.out.println("Cammand added to the queue.");
		}

		JSONObject r = safari.getResponse();
		if (r.getInt("status") != 0) {
			response.setStatus(500);
		}
		write(r, response);

	}

	private void write(JSONObject o, HttpServletResponse resp) throws UnsupportedEncodingException, IOException {
		OutputStream out = resp.getOutputStream();
		out.write(o.toString().getBytes("UTF-8"));

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
