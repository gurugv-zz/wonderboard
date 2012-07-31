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
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class Util {

	public static String publish(String userId,String userPassword) throws Throwable {

		String data = getClipboarddata();
		URL myURL = new URL("http://wonder-board.appspot.com/wbSet?userId="
				+ userId+"&userPassword="+userPassword + "&data=" + URLEncoder.encode(data));
		URLConnection myURLConnection = myURL.openConnection();
		myURLConnection.connect();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				myURLConnection.getInputStream()));
		String result = "";
		String inputLine;
		while ((inputLine = in.readLine()) != null){
			result += inputLine;
		}
		in.close();
		System.out.println(result+" published " + data + " for " + userId);
		return data;
	}

	private static String getClipboarddata() throws UnsupportedFlavorException, Throwable {

		Clipboard clipB = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipB.getContents(null);
		String plainTet = (String) contents.getTransferData(DataFlavor.stringFlavor);
//		BufferedReader reader = new BufferedReader(plainTet);
//		String inputLine;
//		while ((inputLine = reader.readLine()) != null)
//			inputLine += inputLine;
		return plainTet;
	}

	public static String refresh(String userId,String userPassword) throws Throwable {

		URL oracle = new URL("http://wonder-board.appspot.com/wbGet?userId="
				+ userId+"&userPassword="+userPassword);
		URLConnection yc = oracle.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				yc.getInputStream()));
		String data = "";
		String inputLine;
		while ((inputLine = in.readLine()) != null){
			data += inputLine;
		}
		in.close();
		
		System.out.println(" for User Id "+userId+ " : "+data);

		updateClipboard(data);
		return data;
	}

	private static void updateClipboard(String data) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		StringSelection strSel = new StringSelection(data);
		clipboard.setContents(strSel, null);
		System.out.println("updated clipboard with "+data);
	}

	public static boolean authenticate(String userId, String userPass) throws IOException {
		URL oracle = new URL("http://wonder-board.appspot.com/wbSignup?userId="
				+ userId+"&userPassword="+userPass+"&actReq=authenticate");
		HttpURLConnection yc = (HttpURLConnection) oracle.openConnection();
		yc.setRequestMethod("POST");
		BufferedReader in = new BufferedReader(new InputStreamReader(
				yc.getInputStream()));
		String data = "";
		String inputLine;
		while ((inputLine = in.readLine()) != null){
			data += inputLine;
		}
		in.close();
		System.out.println(data);
		return (data.contains("SUCCESS"));
	}
}
