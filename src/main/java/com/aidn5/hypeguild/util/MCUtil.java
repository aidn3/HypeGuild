package com.aidn5.hypeguild.util;

import com.aidn5.hypeguild.ModConfig;
import com.aidn5.hypeguild.testunits.ChatManipulator;

import net.minecraft.client.Minecraft;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.HoverEvent.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class MCUtil {
	public static void sendChatMessage(String message) {
		// TODO: Remove systemPrint in production!
		System.out.println("Executing: " + message);

		if (message.startsWith("/hello")) {
			ChatManipulator.send_hello_command();

		} else if (message.startsWith("/g member ")) {
			String username = message.replaceAll("/g member", "").replaceAll(" ", "").trim();
			ChatManipulator.send_random_member_stat(username);

		} else if (message.startsWith("/g info")) {
			ChatManipulator.send_guild_info();

		} else if (message.startsWith("/g info")) ChatManipulator.send_guild_info();

		// Minecraft.getMinecraft().thePlayer.sendChatMessage(message);
	}

	/**
	 * add styled message to the chat
	 * 
	 * @param message
	 *            the message to show in the chat
	 * @param toolTip
	 *            the text to show on hover
	 */
	public static void showMessage(String message, String toolTip) {
		try {
			StringBuilder wholeMessage = new StringBuilder();

			wholeMessage.append(EnumChatFormatting.YELLOW + "" + EnumChatFormatting.BOLD + ">");
			wholeMessage.append(EnumChatFormatting.WHITE + "" + EnumChatFormatting.BOLD + ModConfig.NAME);
			wholeMessage.append(EnumChatFormatting.YELLOW + "" + EnumChatFormatting.BOLD + "< ");

			wholeMessage.append(EnumChatFormatting.GRAY + "" + EnumChatFormatting.ITALIC + message);

			ChatComponentText chatComponentText = new ChatComponentText(wholeMessage.toString());

			if (toolTip != null) {
				ChatStyle chatStyle = new ChatStyle();

				ChatComponentText hoverText = new ChatComponentText(toolTip);
				HoverEvent hoverEvent = new HoverEvent(Action.SHOW_TEXT, hoverText);
				chatStyle.setChatHoverEvent(hoverEvent);

				chatComponentText.setChatStyle(chatStyle);
			}

			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(chatComponentText);
		} catch (Exception ignored) {}
	}
}
