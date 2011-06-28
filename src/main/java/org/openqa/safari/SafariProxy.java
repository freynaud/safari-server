package org.openqa.safari;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.WebDriverCommand;
import org.openqa.selenium.browserlaunchers.locators.BrowserInstallation;
import org.openqa.selenium.browserlaunchers.locators.SafariLocator;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * represent a safari browser object on the server side.
 * 
 * @author freynaud
 * 
 */
public class SafariProxy {

	private final String session = UUID.randomUUID().toString();
	private DesiredCapabilities capabilities;
	private final BlockingQueue<WebDriverCommand> commands = new LinkedBlockingQueue<WebDriverCommand>();
	private Process safari;
	private boolean ready = false;
	private Lock ext = new ReentrantLock();
	private Condition extReady = ext.newCondition();

	public SafariProxy() {
		System.out.println("created proxy " + session);
	}

	public void launch(DesiredCapabilities cap) {
		SafariLocator locator = new SafariLocator();
		BrowserInstallation install = locator.findBrowserLocation();
		String exe = install.launcherFilePath();
		List<String> cmd = new ArrayList<String>();
		cmd.add(exe);

		try {
			File f = new File(session + ".html");
			FileWriter fstream = new FileWriter(f);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("<html><head><meta http-equiv='refresh' content='0;url=http://localhost:9999/safari-init/" + session + "'  ></head></html>");
			out.close();
			cmd.add(f.getName());
			ProcessBuilder builder = new ProcessBuilder(cmd);

			try {
				SystemWideConfiguration.getInstance().configureSystem(cap);
				safari = builder.start();
			} finally {
				SystemWideConfiguration.getInstance().releaseAndCleanSystem();
			}

		} catch (Throwable t) {
			throw new RuntimeException(t);
		}

		waitForSafariToReportBack();
	}

	private void waitForSafariToReportBack() {
		while (!isReady()) {
			System.out.println("safari not quite ready yet.");
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				//
			}
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

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	JSONObject response;
	boolean waitForExtension = true;

	public void updateResponse(JSONObject resp) {
		this.response = resp;
		try {
			ext.lock();
			extReady.signal();
		} finally {
			ext.unlock();
		}

	}

	public JSONObject getResponse() {
		if (waitForExtension) {
			try {
				ext.lock();
				extReady.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				ext.unlock();
			}
		}
		waitForExtension = true;
		return response;

	}

	public void noResponseFromExtensionExpected(JSONObject json) {
		waitForExtension = false;
		response = json;

	}
}
