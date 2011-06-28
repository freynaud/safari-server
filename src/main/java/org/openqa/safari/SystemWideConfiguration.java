package org.openqa.safari;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.openqa.selenium.remote.DesiredCapabilities;

// TODO freynaud optimize the lock to have no locking for all safari starting on default
public class SystemWideConfiguration {

	private final Lock lock = new ReentrantLock();
	private static final SystemWideConfiguration INSTANCE = new SystemWideConfiguration();
	private static final String defaults = "/usr/bin/defaults";
	private static final String safari = "com.apple.Safari";
	private static final String userAgent = "CustomUserAgent";
	private static boolean systemCurrentlyClean = true;

	private boolean isSystemChangeNeeded(DesiredCapabilities capabilities) {
		String agent = (String) capabilities.getCapability("user-agent");
		if (agent != null) {
			return true;
		} else {
			return false;
		}
	}

	public void configureSystem(DesiredCapabilities capabilities) {
		lock.lock();
		String agent = (String) capabilities.getCapability("user-agent");
		if (agent != null) {
			writeSafariUserAgentDefaults(agent);
			systemCurrentlyClean = false;
		}

	}

	public void releaseAndCleanSystem() {
		try {
			deleteUserAgentDefaults();
		} catch (Throwable t) {
			throw new RuntimeException("The system is potentially is a corrupted state.", t);
		} finally {
			lock.unlock();

		}
	}

	private void writeSafariUserAgentDefaults(String value) {
		writeDefaults(safari, userAgent, value);
	}

	private void deleteUserAgentDefaults() {
		deleteDefaults(safari, userAgent);
	}

	private void writeDefaults(String domain, String key, String value) {
		ProcessBuilder pb = new ProcessBuilder(defaults, "write", domain, key, "-string", value);

		try {
			Process p = pb.start();
			int i = p.waitFor();
			if (i != 0) {
				throw new RuntimeException("Error writing the OS param " + domain + " " + key + " " + value);
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private void deleteDefaults(String domain, String key) {
		if (systemCurrentlyClean) {
			return;
		}
		ProcessBuilder pb = new ProcessBuilder(defaults, "delete", domain, key);

		try {
			Process p = pb.start();
			int i = p.waitFor();
			if (i != 0) {
				throw new RuntimeException("Error delete the OS param " + domain + " " + key + " exit code = " + i);
			} else {
				systemCurrentlyClean = true;
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public static SystemWideConfiguration getInstance() {
		return INSTANCE;
	}
}
