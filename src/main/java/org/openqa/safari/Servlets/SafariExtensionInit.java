package org.openqa.safari.Servlets;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.io.ByteStreams;

public class SafariExtensionInit extends HttpServlet {

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

	private void process(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String path = req.getPathInfo();
		String session = path.replaceFirst("/", "");
		StringBuilder b = new StringBuilder();
		b.append("<html>");
		b.append("<head>");
		b.append("<title>Safari extension init.</title>");
		b.append("<div id='session' >"+session+"</div>");
		b.append("</head>");
		b.append("<body>");
		b.append("</body>");
		b.append("</html>");
		
		write(b.toString(), resp);

	}

	private void write(String content, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(200);

		InputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
		try {
			ByteStreams.copy(in, response.getOutputStream());
		} finally {
			in.close();
			response.getOutputStream().close();
		}
	}

}
