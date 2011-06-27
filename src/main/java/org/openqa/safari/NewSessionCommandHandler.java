package org.openqa.safari;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openqa.Driver;
import org.openqa.selenium.browserlaunchers.locators.BrowserInstallation;
import org.openqa.selenium.browserlaunchers.locators.SafariLocator;

import com.google.common.io.ByteStreams;

public class NewSessionCommandHandler extends CommandHandler {

	private String session;

	public NewSessionCommandHandler(HttpServletRequest request, HttpServletResponse response) {
		super(request, response);
	}

	@Override
	public void process() {
		response.setCharacterEncoding("UTF-8");
		response.setStatus(303);

		SafariLocator locator = new SafariLocator();
		BrowserInstallation install = locator.findBrowserLocation();
		String safari = install.launcherFilePath();
		List<String> cmd = new ArrayList<String>();
		cmd.add(safari);

		String sessionId = UUID.randomUUID().toString();

		try {
			File f = new File("sessionId.html");
			FileWriter fstream = new FileWriter(f);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("<html><head><meta http-equiv='refresh' content='0;url=http://localhost:9999/safari-init/" + sessionId + "'  ></head></html>");
			out.close();
			cmd.add(f.getName());
			ProcessBuilder builder = new ProcessBuilder(cmd);
			Process p = builder.start();
			System.out.println("safari started, session = " + sessionId);
			response.addHeader("location", "/session/" + sessionId);
			session = sessionId;

		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public String getSession() {
		return session;
	}
}
