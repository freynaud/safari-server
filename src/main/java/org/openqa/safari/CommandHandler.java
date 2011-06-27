package org.openqa.safari;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class CommandHandler {

	protected HttpServletRequest request;
	protected HttpServletResponse response;

	public CommandHandler(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	public abstract void process();

}
