package org.openqa.safari;

import org.json.JSONException;
import org.json.JSONObject;

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

	public static JSONObject getResponse(String session, int status, JSONObject value) {
		try {
			JSONObject res = new JSONObject();
			res.put("sessionId", session);
			res.put("status", status);
			if (value != null) {
				res.put("value", value);
			} else {
				res.put("value", JSONObject.NULL);
			}
			return res;
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}

	}
}
