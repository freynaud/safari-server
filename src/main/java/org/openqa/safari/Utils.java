package org.openqa.safari;

public class Utils {

	public static String extractSessionFromPath(String path) {
		if (path == null) {
			return null;
		}
		int sessionIndex = path.indexOf("/session/");
		if (sessionIndex != -1) {
			sessionIndex += "/session/".length();
			int nextSlash = path.indexOf("/", sessionIndex);
			String session = null;
			if (nextSlash != -1) {
				session = path.substring(sessionIndex, nextSlash);
			} else {
				session = path.substring(sessionIndex, path.length());
			}
			if ("".equals(session)) {
				return null;
			}
			return session;
		}
		return null;
	}
}
