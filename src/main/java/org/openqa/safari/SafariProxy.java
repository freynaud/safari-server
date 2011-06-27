package org.openqa.safari;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.openqa.WebDriverCommand;
import org.openqa.selenium.browserlaunchers.locators.BrowserInstallation;
import org.openqa.selenium.browserlaunchers.locators.SafariLocator;
import org.openqa.selenium.remote.DesiredCapabilities;

public class SafariProxy {

	private final String session = UUID.randomUUID().toString();
	private DesiredCapabilities capabilities;
	private final BlockingQueue<WebDriverCommand> commands = new LinkedBlockingQueue<WebDriverCommand>();
	private Process safari;

	public SafariProxy() {

	}

	public void launch() {
		SafariLocator locator = new SafariLocator();
		BrowserInstallation install = locator.findBrowserLocation();
		String exe = install.launcherFilePath();
		List<String> cmd = new ArrayList<String>();
		cmd.add(exe);

		try {
			File f = new File("sessionId.html");
			FileWriter fstream = new FileWriter(f);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("<html><head><meta http-equiv='refresh' content='0;url=http://localhost:9999/safari-init/" + session + "'  ></head></html>");
			out.close();
			cmd.add(f.getName());
			ProcessBuilder builder = new ProcessBuilder(cmd);
			safari = builder.start();

		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public void quit() {
		safari.destroy();
	}

	public String getSession() {
		return session;
	}

	public void addCommand(WebDriverCommand command) {
		commands.add(command);
	}

	public BlockingQueue<WebDriverCommand> getCommandQueue() {
		return commands;
	}
}
