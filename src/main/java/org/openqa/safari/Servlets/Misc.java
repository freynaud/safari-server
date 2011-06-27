package org.openqa.safari.Servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openqa.safari.CommandHandlerFactory;

public class Misc extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	}

	private CommandHandlerFactory factory = new CommandHandlerFactory();

	private void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("got on misc :"+request.getMethod()+" on "+request.getPathInfo());
		throw new RuntimeException("wrong url");

	}

}
