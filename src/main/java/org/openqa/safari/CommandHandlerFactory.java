package org.openqa.safari;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CommandHandlerFactory {
	public static CommandHandler getHandler(HttpServletRequest request, HttpServletResponse response){
		String path = request.getPathInfo();
		System.out.println(path);
		return new NewSessionCommandHandler(request,response);
	}
}
