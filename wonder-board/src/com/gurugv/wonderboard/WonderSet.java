package com.gurugv.wonderboard;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class WonderSet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		String userId = req.getParameter(Constants.PARAM_USERID);
		String password = req.getParameter(Constants.PARAM_USERPASS);
		if (userId == null || password == null) {
			resp.getWriter().println("No authentication");
			return;
		}

		String data = req.getParameter(Constants.PARAM_DATAT);
		if (data == null) {
			resp.getWriter().println("no data to set!");
			return;
		}

		String contextKey = getKey(userId, password);
		ClipboardStorageService clipBoardService = (ClipboardStorageService) req
				.getSession().getServletContext().getAttribute(contextKey);
		if (clipBoardService == null) {
			boolean success = UserStorageService.getInstance().authenticate(userId,
					password);
			if (!success) {
				resp.getWriter().println("Authentication failed");
				return;
			}
			clipBoardService = new ClipboardStorageService(userId, password);
			req.getSession().getServletContext()
					.setAttribute(contextKey, clipBoardService);
		}
		clipBoardService.updateNewClipboardItem(data);
		resp.getWriter().println("Success: "+data);
	}

	String getKey(String userId, String password) {
		return userId + ";" + password;
	}
}
