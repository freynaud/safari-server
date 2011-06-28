package org.openqa;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.safari.Utils;

public class WebDriverCommand {

	private String method;
	private String path;
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
			res.put("genericPath", Utils.getGenericPath(path));
			
			String elementId = Utils.extractElementIdFromPath(path);
			if (elementId!=null){
				res.put("id", elementId);
			}
			
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
		return "get".equalsIgnoreCase(method) && "/session/:sessionId".equals(Utils.getGenericPath(path));
	}
}
