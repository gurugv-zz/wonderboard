package com.gurugv.wonderboard;

import java.io.IOException;
import javax.servlet.http.*;

import org.mortbay.util.ajax.JSON;

@SuppressWarnings("serial")
public class WonderShare extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		String userId = req.getParameter(Constants.PARAM_USERID);
		String userIdToShareWith = req
				.getParameter(Constants.PARAM_USERID_TO_SHARE);
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

		if (userIdToShareWith == null) {
			resp.getWriter().println("no user to share with");
			return;
		}

		String contextKey = getKey(userId, password);
		ClipboardStorageService clipBoardService = (ClipboardStorageService) req
				.getSession().getServletContext().getAttribute(contextKey);
		if (clipBoardService == null) {
			boolean success = UserStorageService.getInstance().authenticate(
					userId, password);
			if (!success) {
				resp.getWriter().println("Authentication failed");
				return;
			}
			clipBoardService = new ClipboardStorageService(userId, password);
			req.getSession().getServletContext()
					.setAttribute(contextKey, clipBoardService);
		}
		clipBoardService.shareClipboardItem(userIdToShareWith, data);
		resp.getWriter().println(
				"Success: " + data + " Shared with " + userIdToShareWith);
	}

	String getKey(String userId, String password) {
		return userId + ";" + password;
	}
}
