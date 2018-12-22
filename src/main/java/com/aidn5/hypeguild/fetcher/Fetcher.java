package com.aidn5.hypeguild.fetcher;

import java.io.IOException;
import java.util.List;

import com.aidn5.hypeguild.HypeGuild;
import com.aidn5.hypeguild.ModConfig;
import com.aidn5.hypeguild.models.GuildMember;
import com.google.gson.JsonParseException;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

/**
 * Fetches data from Hypixel's API.
 * 
 * @author aidn5
 * @see https://api.hypixel.net
 */
public abstract class Fetcher {
	private static final String GUILD_STRING_CACHE = "guildStringCache";

	String errorMessage = null;
	String errorToolTip = "hi :)";

	private String guildString;
	private String guildStringCache;

	/**
	 * get the members of the user guild
	 * 
	 * @return all members of the guild
	 */
	protected List<GuildMember> getGuildMembers(boolean forceRefresh) {
		if (guildStringCache == null) loadGuildCache();

		try {
			if (forceRefresh || guildStringCache == null) {

				String uuid = Minecraft.getMinecraft().getSession().getPlayerID();
				String apiKey = HypeGuild.instance.settings.getProperty("api", null);

				if (apiKey == null) {
					giveNoApiError();
					return null;
				}

				String guildId = Guild.findGuildIdByMemberUuid(uuid, apiKey);
				if (guildId == null) {
					giveNoGuildFoundError();
					return null;
				}

				guildString = Guild.getGuildStringById(guildId, apiKey);
			} else {
				guildString = guildStringCache;
			}

			List<GuildMember> guildMembers = Guild.getGuildMemebersFromString(guildString);

			HypeGuild.instance.cacheController.save(GUILD_STRING_CACHE, guildString);

			return guildMembers;

		} catch (IOException e) {
			e.printStackTrace();

			errorMessage = EnumChatFormatting.RED + "Check your Internet connection...";
			errorToolTip = e.getClass().getName() + ": " + e.getMessage();

		} catch (JsonParseException e) {
			e.printStackTrace();

			errorMessage = EnumChatFormatting.RED + "Can not parse/get members of the guild :(";
			errorToolTip = e.getClass().getName() + ": " + e.getMessage();

		} catch (Exception e) {
			e.printStackTrace();

			errorMessage = EnumChatFormatting.RED + "Something went Wrong...";
			errorToolTip = e.getClass().getName() + ": " + e.getMessage();

		}

		if (!forceRefresh && guildStringCache != null) {
			errorMessage = EnumChatFormatting.GRAY + "Btw. Cache is ON!";
			errorToolTip = "You can ignore the cache by adding 'forceRefresh to your command ;)";
		}

		return null;
	}

	protected void loadUsernames(List<GuildMember> guildMembers) {
		for (GuildMember guildMember : guildMembers) {
			String uuid = guildMember.uuid;
			String username = HypeGuild.instance.ignUuidResolver.getUsername(uuid);

			if (username != null && !username.isEmpty()) {
				guildMember.username = username;
			}
		}
	}

	private void loadGuildCache() {
		guildStringCache = HypeGuild.instance.cacheController.get(GUILD_STRING_CACHE);
	}

	/**
	 * Show error in chat when the hypixel api key is not defined
	 */
	private void giveNoApiError() {
		errorMessage = EnumChatFormatting.RED + "ERROR: Can not find Hypixel API key!";

		errorToolTip = "Do '/" + ModConfig.MODID + " setApiKey' for new key\n";
		errorToolTip += "Or '/" + ModConfig.MODID + " setApiKey [~api] to sign existed key";

	}

	/**
	 * Show error in the chat when guild id coudln't be found
	 */
	private void giveNoGuildFoundError() {
		errorMessage = "Are you sure you are in a guild?";

		errorToolTip = "I could not find the guild you are in... :/";

	}
}
