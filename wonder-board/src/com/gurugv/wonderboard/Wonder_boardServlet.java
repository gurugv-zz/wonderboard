package com.gurugv.wonderboard;

import java.io.IOException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class Wonder_boardServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
	}
}
