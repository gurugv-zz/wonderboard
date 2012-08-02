package com.gurugv.wonderboard;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.*;

import org.mortbay.util.ajax.JSON;

import com.google.gson.Gson;

@SuppressWarnings("serial")
public class WonderShareGet extends HttpServlet {

	

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		String userId = req.getParameter(Constants.PARAM_USERID);
		String password = req.getParameter(Constants.PARAM_USERPASS);
		if (userId == null || password == null) {
			resp.getWriter().println("No authentication");
			return;
		}

		boolean success = UserStorageService.getInstance().authenticate(userId,
				password);
		if (!success) {
			resp.getWriter().println("Authentication failed");
			return;
		}

		HashMap<String, String> sharedClipItemsMap = SharedClipboardItems
				.getInstance().getSharedClipboards(userId);
		if (sharedClipItemsMap == null || sharedClipItemsMap.isEmpty()) {
			resp.getWriter().println(Constants.NOT_AVIALABLE);
		} else {
			Gson gson = new Gson();
			String rsponse = gson.toJson(sharedClipItemsMap);
			System.out.println(rsponse);
			resp.getWriter().println(rsponse);
		}

	}

}
