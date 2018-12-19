package com.aidn5.hypeguild.fetcher;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.aidn5.hypeguild.HypeGuild;
import com.aidn5.hypeguild.ModConfig;
import com.aidn5.hypeguild.models.GuildMember;
import com.google.gson.JsonParseException;

import net.minecraft.client.Minecraft;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.HoverEvent.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class Fetcher {
	private static final String GUILD_STRING_CACHE = "guildStringCache";

	/**
	 * get the members of the user guild
	 * 
	 * @return all members of the guild
	 */
	protected static List<GuildMember> getGuildMembers(boolean forceRefresh) {
		String guildString;
		String guildStringCache = HypeGuild.instance.cacheController.get(GUILD_STRING_CACHE);

		try {
			if (forceRefresh || guildStringCache == null) {

				String uuid = Minecraft.getMinecraft().getSession().getPlayerID();
				String apiKey = HypeGuild.instance.settings.getProperty("api", null);

				if (apiKey == null) {
					showNoApiError();
					return null;
				}

				String guildId = Guild.findGuildIdByMemberUuid(uuid, apiKey);
				if (guildId == null) {
					showNoGuildFoundError();
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
			String message = EnumChatFormatting.RED + "Check your Internet connection...";
			String toolTip = e.getClass().getName() + ": " + e.getMessage();

			showMessage(message, toolTip);
		} catch (JsonParseException e) {
			e.printStackTrace();
			String message = EnumChatFormatting.RED + "Can not parse/get members of the guild :(";
			String toolTip = e.getClass().getName() + ": " + e.getMessage();

			showMessage(message, toolTip);
		} catch (Exception e) {
			e.printStackTrace();
			String message = EnumChatFormatting.RED + "Something went Wrong...";
			String toolTip = e.getClass().getName() + ": " + e.getMessage();

			showMessage(message, toolTip);
		}

		if (!forceRefresh && guildStringCache != null) {
			String message = EnumChatFormatting.GRAY + "Cache is ON!";
			String toolTip = "You can ignore the cache by adding 'forceRefresh to the command ;)";

			showMessage(message, toolTip);
		}

		return null;
	}

	protected static void sortMembers(List<GuildMember> guildMembers) {

		Collections.sort(guildMembers, new Comparator<GuildMember>() {

			@Override
			public int compare(GuildMember o1, GuildMember o2) {
				return compareMembers(o1, o2);
			}
		});
	}

	protected static int compareMembers(GuildMember o1, GuildMember o2) {
		// First in rank priority
		if (o1.rankProirity > o2.rankProirity) return -1;
		else if (o1.rankProirity < o2.rankProirity) return 1;

		// If the same, who tag has
		if (!o1.tag.isEmpty() && o2.tag.isEmpty()) return -1;
		else if (o1.tag.isEmpty() && !o2.tag.isEmpty()) return 1;

		// See in name?!
		return o1.rank.toLowerCase().compareTo(o1.rank.toLowerCase());
	}

	/**
	 * add styled message to the chat
	 * 
	 * @param message
	 *            the message to show in the chat
	 * @param toolTip
	 *            the text to show on hover
	 */
	protected static void showMessage(String message, String toolTip) {
		try {
			StringBuilder wholeMessage = new StringBuilder();

			wholeMessage.append(EnumChatFormatting.YELLOW + "" + EnumChatFormatting.BOLD + ">");
			wholeMessage.append(EnumChatFormatting.WHITE + "" + EnumChatFormatting.BOLD + ModConfig.NAME);
			wholeMessage.append(EnumChatFormatting.YELLOW + "" + EnumChatFormatting.BOLD + "< ");

			wholeMessage.append(EnumChatFormatting.GRAY + "" + EnumChatFormatting.ITALIC + message);

			ChatComponentText chatComponentText = new ChatComponentText(wholeMessage.toString());
			ChatStyle chatStyle = new ChatStyle();

			ChatComponentText hoverText = new ChatComponentText(toolTip);
			HoverEvent hoverEvent = new HoverEvent(Action.SHOW_TEXT, hoverText);
			chatStyle.setChatHoverEvent(hoverEvent);

			chatComponentText.setChatStyle(chatStyle);

			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(chatComponentText);
		} catch (Exception ignored) {}
	}

	/**
	 * Show error in chat when the hypixel api key is not defined
	 */
	private static void showNoApiError() {
		String message = EnumChatFormatting.RED + "ERROR: Can not find Hypixel API key!";

		String toolTip = "Do '/" + ModConfig.MODID + " setApiKey' for new key\n";
		toolTip += "Or '/" + ModConfig.MODID + " setApiKey [~api] to sign existed key";

		showMessage(message, toolTip);
	}

	/**
	 * Show error in the chat when guild id coudln't be found
	 */
	protected static void showNoGuildFoundError() {
		String message = "Are you sure you are in a guild?";

		String toolTip = "I could not find the guild you are in... :/";

		showMessage(message, toolTip);
	}
}
