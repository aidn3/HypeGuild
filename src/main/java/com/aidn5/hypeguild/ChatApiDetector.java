package com.aidn5.hypeguild;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aidn5.hypeguild.util.Settings;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Listen to chat and search for hypixel api and save it when new one has been
 * requested
 * 
 * @author aidn5
 *
 */
class ChatApiDetector {
	private static final Pattern apiKeyPattern = Pattern.compile("^Your new API key is ([A-Za-z0-9\\-]{36})");

	private final Settings settings;

	ChatApiDetector(Settings settings) {
		this.settings = settings;
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void onPlayerChatReceive(ClientChatReceivedEvent event) {
		if (event.type != 0) return; // Its not from/for the chat

		Matcher matcher = apiKeyPattern.matcher(event.message.getUnformattedText());
		if (matcher.find()) {
			settings.setProperty("api", matcher.group(0));
			System.out.println("The new api has been saved!");
			settings.SaveUserSettings();
		}
	}
}
