package com.aidn5.hypeguild.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Util {

	/**
	 * open stream, request data from URL and return the response as String
	 * 
	 * @param url
	 *            the requested URL
	 * @return String
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static String requestData(String url) throws MalformedURLException, IOException {
		StringBuilder response = new StringBuilder();

		URLConnection urlConnection = new URL(url).openConnection();
		InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		String inputLine = "";
		while ((inputLine = bufferedReader.readLine()) != null)
			response.append(inputLine);

		bufferedReader.close();
		inputStreamReader.close();

		return response.toString();
	}

}
