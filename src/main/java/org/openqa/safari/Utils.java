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

	public static String extractElementIdFromPath(String path) {
		if (path == null) {
			return null;
		}
		int elementIndex = path.indexOf("/element/");
		if (elementIndex != -1) {
			elementIndex += "/element/".length();
			int nextSlash = path.indexOf("/", elementIndex);
			String el = null;
			if (nextSlash != -1) {
				el = path.substring(elementIndex, nextSlash);
			} else {
				el = path.substring(elementIndex, path.length());
			}
			if ("".equals(el)) {
				return null;
			}
			return el;
		}
		return null;
	}

	public static String getGenericPath(String path) {
		String res = path;
		String session = extractSessionFromPath(path);
		if (session != null) {
			res = res.replace(session, ":sessionId");
		}
		String elementId = extractElementIdFromPath(path);
		if (elementId != null) {
			res = res.replaceAll(elementId, ":id");
		}
		return res;

	}
}
