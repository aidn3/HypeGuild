package com.aidn5.hypeguild.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.NameNotFoundException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class IgnUuidResolver {
	private List<User> users;
	private final File cacheFile;

	public static long cacheTime = 60 * 60 * 24 * 3;// save username for 3 days

	public IgnUuidResolver(File cacheFile) {
		this.cacheFile = cacheFile;
	}

	public String getUsername(String uuid) {
		if (uuid == null) return null;

		long cacheTime = (System.currentTimeMillis() - (this.cacheTime * 1000)) / 1000L;

		for (Iterator<User> iterator = users.iterator(); iterator.hasNext();) {
			User user = iterator.next();
			if (user.uuid == null || user.username == null) {
				iterator.remove();

			} else if (user.uuid.equals(uuid)) {
				if (cacheTime < user.reslovedTime) {
					return user.username;
				} else {
					iterator.remove();
				}
			}
		}

		try {
			String username = this.uuidToUsername(uuid);
			User user = new User();
			user.uuid = uuid;
			user.username = username;
			user.reslovedTime = System.currentTimeMillis() / 1000L;
			this.users.add(user);

			return username;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void load() {
		try {
			FileInputStream fis = new FileInputStream(this.cacheFile);

			byte[] data = new byte[(int) this.cacheFile.length()];
			fis.read(data);
			fis.close();

			String string = new String(data, "UTF-8");

			Type listType = new TypeToken<List<User>>() {}.getType();

			Gson gson = new Gson();
			this.users = gson.fromJson(string, listType);
			return;

		} catch (Exception e) {
			e.printStackTrace();
		}
		this.users = new ArrayList<User>();
	}

	public void save() {
		try {
			Gson gson = new Gson();
			Type listType = new TypeToken<List<User>>() {}.getType();
			String json = gson.toJson(this.users, listType);

			OutputStream out = new FileOutputStream(this.cacheFile);
			out.write(json.getBytes());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param uuid
	 * @return String username of the uuid
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws NameNotFoundException
	 *             if the username not exists
	 */
	private String uuidToUsername(String uuid) throws MalformedURLException, IOException, NameNotFoundException {
		String url = "https://api.mojang.com/user/profiles/" + uuid.replace("-", "") + "/names";
		String response = Util.requestData(url);

		if (response.isEmpty()) throw new NameNotFoundException();

		JsonArray jsonArray = new JsonParser().parse(response).getAsJsonArray();
		return jsonArray.get(0).getAsJsonObject().get("name").getAsString();
	}

	/**
	 * 
	 * @param username
	 * @return String the UUID of the username
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws NameNotFoundException
	 */
	private String usernameToUuid(String username) throws MalformedURLException, IOException, NameNotFoundException {
		String response = Util.requestData("https://api.mojang.com/users/profiles/minecraft/" + username);

		if (response.isEmpty()) throw new NameNotFoundException();

		JsonElement jsonElement = new JsonParser().parse(response);
		return jsonElement.getAsJsonObject().get("id").getAsString();
	}

	private class User {
		private String username;
		private String uuid;
		private long reslovedTime;
	}
}
