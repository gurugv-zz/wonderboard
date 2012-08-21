package com.gurugv.wonderboard.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class UrlServiceUtil {

	public static final String SERVER = "http://wonder-board.appspot.com/";
//	public static final String SERVER = "http://localhost:8888/";
	private final String servlet;
	private final String urlStr;
	private final HashMap<String, String> params = new HashMap<String, String>();

	public UrlServiceUtil(String servlet) {
		this.servlet = servlet;
		this.urlStr = SERVER + servlet;
	}

	public void addParam(String key, String value) {
		params.put(key, value);
	}

	private String ceateParamStringRequest() {
		boolean hasParams = false;
		Iterator<Entry<String, String>> itr = params.entrySet().iterator();
		String urlWithParams = "";
		while (itr.hasNext()) {
			Entry<String, String> entry = itr.next();
			String key = entry.getKey();
			String value = entry.getValue();
			if (!hasParams) {
				// urlWithParams += "?";
				hasParams = true;
			} else {
				urlWithParams += "&";
			}
			try {
				urlWithParams += URLEncoder.encode(key, "UTF-8") + "="
						+ URLEncoder.encode(value, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				urlWithParams += key + "=" + value;
			}
		}
		return urlWithParams;
	}

	public String callGet() throws MalformedURLException, IOException {
		return urlGETCallWithResult(new URL(
				generateUrlWithParamsForGetRequest()));
	}

	private String generateUrlWithParamsForGetRequest() {
		String paramString = ceateParamStringRequest();
		return urlStr + (paramString.isEmpty() ? "" : ("?" + paramString));
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

	public String callPost() throws IOException {

		URL url = new URL(urlStr);

		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-type",
				"application/x-www-form-urlencoded");
		String toServer = ceateParamStringRequest();
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
		return data;
	}

}
