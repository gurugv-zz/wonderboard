package com.gurugv.wonderboard;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class WbSignup extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/plain");
		System.out.println("asdsad ");
		String userId = (String) req.getParameter(Constants.PARAM_USERID);
		String userPass = (String) req.getParameter(Constants.PARAM_USERPASS);
		String requestedAction = (String) req
				.getParameter(Constants.PARAM_ACTION);
		System.out.println("11 userId: "+userId+"; userPass: "+userPass+"; requestedAction: "+requestedAction);
		if (requestedAction!= null && requestedAction.equals("validate")) {
			String errorMessage = doValidation(userId, userPass);
			if (errorMessage != null) {
				resp.getWriter().println(errorMessage);
			}
		}if (requestedAction!= null && requestedAction.equals("authenticate")) {
			boolean isSuccess= UserStorageService.getInstance().authenticate(userId, userPass);
			if (!isSuccess) {
				resp.getWriter().println("FAILED");
			}else{
				resp.getWriter().println("SUCCESS");
				
			}
		} else { // signup
			String errorMessage = doSignup(userId, userPass);
			if (errorMessage != null) {
				resp.getWriter().println(errorMessage);
			}
		}
	}

	private String doSignup(String userId, String userPass) {
		String validationMessage = doValidation(userId, userPass);
		if(validationMessage != null)
			return validationMessage;
		boolean success = UserStorageService.getInstance().createAccount(userId,userPass);
		if(!success){
			return "Error occured while Creating user";
		}
		return null;
	}

	private String doValidation(String userId, String userPass) {
		boolean exists = UserStorageService.getInstance().checkIfExists(userId);
		if(exists){
			return "UserId already Exists";
		}
		return null;
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		resp.getWriter().println("Illegal");
	}
}
