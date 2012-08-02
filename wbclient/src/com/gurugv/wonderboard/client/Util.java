package com.gurugv.wonderboard.client;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import com.google.gson.Gson;


public class Util {

	private static final String PARAM_USER_ID_TO_SHARE = "userIdToShare";
	private static final String PARAM_DATA = "data";
	private static final String PARAM_USER_PASSWORD = "userPassword";
	private static final String PARAM_USER_ID = "userId";
	private static final Gson gson = new Gson();

	public static String shareCurrentClipboardTo(String currentUserId,
			String userPassword, String shareTouserId)
			throws UnsupportedFlavorException, Throwable {

		String data = getClipboarddata();

		UrlServiceUtil caller = new UrlServiceUtil("wbShare");
		caller.addParam(PARAM_USER_ID, currentUserId);
		caller.addParam(PARAM_USER_PASSWORD, userPassword);
		caller.addParam(PARAM_DATA, data);
		caller.addParam(PARAM_USER_ID_TO_SHARE, shareTouserId);

		String result = caller.callGet();
		return result;

	}

	public static String publish(String userId, String userPassword)
			throws Throwable {

		String data = getClipboarddata();
		UrlServiceUtil caller = new UrlServiceUtil("wbSet");
		caller.addParam(PARAM_USER_ID, userId);
		caller.addParam(PARAM_USER_PASSWORD, userPassword);
		caller.addParam(PARAM_DATA, data);

		String result = caller.callGet();
		System.out.println(result + " published " + data + " for " + userId);
		return data;
	}

	private static String getClipboarddata() throws UnsupportedFlavorException,
			Throwable {

		Clipboard clipB = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipB.getContents(null);
		String plainTet = (String) contents
				.getTransferData(DataFlavor.stringFlavor);
		// BufferedReader reader = new BufferedReader(plainTet);
		// String inputLine;
		// while ((inputLine = reader.readLine()) != null)
		// inputLine += inputLine;
		return plainTet;
	}

	public static String refresh(String userId, String userPassword)
			throws Throwable {

		UrlServiceUtil caller = new UrlServiceUtil("wbGet");
		caller.addParam(PARAM_USER_ID, userId);
		caller.addParam(PARAM_USER_PASSWORD, userPassword);

		String data = caller.callGet();
		System.out.println(" for User Id " + userId + " : " + data);
		if (!data.equals(Constants.NOT_AVIALABLE)) {
			updateClipboard(data);
		}
		return data;
	}

	public static void updateClipboard(String data) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		StringSelection strSel = new StringSelection(data);
		clipboard.setContents(strSel, null);
		System.out.println("updated clipboard with " + data);
	}

	public static boolean authenticate(String userId, String userPass)
			throws IOException {
		URL oracle = new URL(UrlServiceUtil.SERVER + "wbSignup");

		String toServer = "userId=" + userId + "&userPassword=" + userPass
				+ "&actReq=authenticate";
		HttpURLConnection con = (HttpURLConnection) oracle.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-type",
				"application/x-www-form-urlencoded");
		con.setRequestProperty("Content-length", "" + toServer.length());
		con.setDoOutput(true);
		con.setUseCaches(false);
		OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
		wr.write(toServer);
		wr.flush();
		wr.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String data = "";
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			data += inputLine;
		}
		in.close();
		System.out.println(data);
		return (data.contains("SUCCESS"));
	}

	public static HashMap<String, String> refreshShared(String userId,
			String userPassword) throws MalformedURLException, IOException {

		UrlServiceUtil caller = new UrlServiceUtil("wbShareGet");
		caller.addParam(PARAM_USER_ID, userId);
		caller.addParam(PARAM_USER_PASSWORD, userPassword);
		String result = caller.callGet();

		if (result == null || result.isEmpty()
				|| result.equals(Constants.NOT_AVIALABLE)) {
			return new HashMap<String, String>();
		}

		HashMap<String, String> sharedUserDataMap = gson.fromJson(result,
				new HashMap<String, String>().getClass());
		return sharedUserDataMap;
	}

}
