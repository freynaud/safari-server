package org.openqa;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.safari.Utils;

public class WebDriverCommand {

	private String method;
	private String path;
	private String genericPath;
	private JSONObject content;

	public WebDriverCommand() {

	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
		String session = Utils.extractSessionFromPath(path);
		if (session != null) {
			genericPath = path.replace(session, ":sessionId");
		} else {
			genericPath = path;
		}
	}

	public JSONObject getContent() {
		return content;
	}

	public void setContent(JSONObject content) {
		this.content = content;
	}

	public boolean isNewSession() {
		return "post".equalsIgnoreCase(method) && "/session".equalsIgnoreCase(path);
	}

	public boolean isDeleteSession() {
		return "delete".equalsIgnoreCase(method) && path.startsWith("/session");
	}

	public String getSession() {
		return Utils.extractSessionFromPath(path);
	}

	public JSONObject toJSON() {
		try {
			JSONObject res = new JSONObject();
			res.put("method", method);
			res.put("path", path);
			res.put("genericPath", genericPath);
			if (content == null) {
				res.put("content", JSONObject.NULL);
			} else {
				res.put("content", content);

			}
			return res;
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}

	}

	public boolean isGetSession() {
		return "get".equalsIgnoreCase(method) && "/session/:sessionId".equals(genericPath);
	}
}
