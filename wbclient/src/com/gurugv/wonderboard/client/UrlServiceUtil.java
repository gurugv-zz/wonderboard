package com.gurugv.wonderboard.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class UrlServiceUtil {

	public static final String SERVER = "http://wonder-board.appspot.com/";
	private final String servlet;
	private String urlStr;
	private boolean hasParams = false;

	public UrlServiceUtil(String servlet) {
		this.servlet = servlet;
		this.urlStr = SERVER + servlet;
	}

	public void addParam(String key, String value) {
		if (!hasParams) {
			urlStr += "?";
			hasParams = true;
		} else {
			urlStr += "&";
		}
		try {
			urlStr += URLEncoder.encode(key, "UTF-8") + "="
					+ URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			urlStr += key + "=" + value;
		}
	}
	
	public String callGet() throws MalformedURLException, IOException{
		return urlGETCallWithResult(new URL(urlStr));
	}
	

	private String urlGETCallWithResult(URL myURL) throws IOException {
		URLConnection myURLConnection = myURL.openConnection();
		myURLConnection.connect();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				myURLConnection.getInputStream()));
		String result = "";
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			result += inputLine;
		}
		in.close();
		return result;
	}

}
