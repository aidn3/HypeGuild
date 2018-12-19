package com.aidn5.hypeguild.fetcher;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.aidn5.hypeguild.models.GuildMember;
import com.aidn5.hypeguild.util.Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * Responsible on fetching the data from Hypixel Api
 * 
 * @author aidn5
 *
 */
public class Guild {
	/**
	 * @param uuid
	 *            a member of the guild
	 * 
	 * @return guild's id
	 * 
	 * 
	 * @throws IOException
	 *             on error with the Internet connection
	 * @throws MalformedURLException
	 *             (This should never happen).
	 */
	public static String findGuildIdByMemberUuid(String uuid, String apiKey) throws MalformedURLException, IOException {

		String url = "https://api.hypixel.net/findGuild?key=" + apiKey + "&byUuid=" + uuid;
		String response = Util.requestData(url);

		JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
		if (jsonObject.get("guild").isJsonNull()) return null;

		return jsonObject.get("guild").getAsString();
	}

	// @return {@link List}[{@link Player}] with all members of the guild
	/**
	 * Retrieve the string from Hypixel's Official Api
	 * 
	 * @param id
	 *            Guild's id
	 * @param apiKey
	 *            Hypixel api's key
	 * 
	 * @return JSON as string
	 * 
	 * @throws IOException
	 *             on error with the Internet connection
	 * @throws MalformedURLException
	 *             (This should never happen).
	 */
	public static String getGuildStringById(String id, String apiKey) throws MalformedURLException, IOException {

		String url = "https://api.hypixel.net/guild?key=" + apiKey + "&id=" + id;
		String response = Util.requestData(url);

		return response;
	}

	/**
	 * Fetch the data from the JSON string and create a list from it
	 * 
	 * @param json
	 *            JSON string
	 * 
	 * @return {@link List}[{@link GuildMember}] with all the members of the guild
	 * 
	 * @throws JsonParseException
	 *             if the specified text is not valid JSON
	 */
	public static List<GuildMember> getGuildMemebersFromString(String json) throws JsonParseException {
		JsonObject jsonGuild = new JsonParser().parse(json).getAsJsonObject().get("guild").getAsJsonObject();
		JsonArray jsonMembers = jsonGuild.get("members").getAsJsonArray();

		List<GuildMember> guildMembers = new ArrayList<GuildMember>();

		for (int i = 0; i < jsonMembers.size(); i++) {
			JsonObject jsonMember = jsonMembers.get(i).getAsJsonObject();

			GuildMember guildMember = new GuildMember();
			guildMembers.add(guildMember);

			guildMember.guildName = jsonGuild.get("name").getAsString();
			guildMember.uuid = jsonMember.get("uuid").getAsString();
			guildMember.rank = jsonMember.get("rank").getAsString();
			guildMember.joinedAt = jsonMember.get("joined").getAsInt();
			guildMember.rank = jsonMember.get("rank").getAsString().toLowerCase();
			guildMember.tag = getRankTag(guildMember.rank, jsonGuild.get("ranks").getAsJsonArray());
			guildMember.tagColor = jsonGuild.get("tagColor").getAsString();
			guildMember.rankProirity = getRankPriority(guildMember.rank, jsonGuild.get("ranks").getAsJsonArray());
		}

		return guildMembers;
	}

	private static String getRankTag(String rank, JsonArray ranks) {
		rank = rank.toLowerCase();

		for (int i = 0; i < ranks.size(); i++) {
			JsonObject jsonRank = ranks.get(i).getAsJsonObject();
			if (rank.equals(jsonRank.get("name").getAsString().toLowerCase())) {
				if (jsonRank.get("tag").isJsonNull()) {
					return "";
				} else {
					return jsonRank.get("tag").getAsString();
				}
			}
		}

		return "";
	}

	private static int getRankPriority(String rank, JsonArray ranks) {
		rank = rank.toLowerCase();

		for (int i = 0; i < ranks.size(); i++) {
			JsonObject jsonRank = ranks.get(i).getAsJsonObject();
			if (rank.equals(jsonRank.get("name").getAsString().toLowerCase())) {
				return jsonRank.get("priority").getAsInt();
			}
		}

		return 10;// GuildMaster does not exist in the ranks list
	}
}
