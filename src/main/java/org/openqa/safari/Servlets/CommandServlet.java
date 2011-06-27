package org.openqa.safari.Servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.Driver;
import org.openqa.WebDriverCommand;
import org.openqa.safari.CommandHandlerFactory;
import org.openqa.safari.NewSessionCommandHandler;
import org.openqa.safari.SafariProxy;
import org.openqa.safari.Utils;

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
		if (command.isNewSession()) {
			SafariProxy safari = new SafariProxy();
			safari.launch();
			String session = safari.getSession();
			Driver.safaris.put(session, safari);
			response.addHeader("location", "/session/" + session);

		} else if (command.isDeleteSession()) {
			String session = Utils.extractSessionFromPath(request.getPathInfo());
			SafariProxy safari = Driver.safaris.get(session);
			safari.quit();
		} else {
			String session = Utils.extractSessionFromPath(request.getPathInfo());
			SafariProxy safari = Driver.safaris.get(session);
			safari.addCommand(command);
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
