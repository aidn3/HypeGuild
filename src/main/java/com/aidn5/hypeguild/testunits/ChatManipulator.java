package com.aidn5.hypeguild.testunits;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * This class helps in debugging. This must never go with the production/release
 * 
 * @author aidn5
 *
 */
public class ChatManipulator {

	public static void send_random_member_stat(String name) {
		sendChatEvent("                                   The Skys");
		sendChatEvent("");
		sendChatEvent("                                 [VIP+] " + name);
		sendChatEvent("");
		sendChatEvent("");
		sendChatEvent("Rank: Member");
		sendChatEvent("Joined: 2018-09-22 20:52 EDT");
		sendChatEvent("Guild Exp Contributions:");
		sendChatEvent("    Today:           " + getRandomNr() + " Guild Experience");
		sendChatEvent("    Dec 18 2018:   " + getRandomNr() + " Guild Experience");
		sendChatEvent("    Dec 17 2018:   " + getRandomNr() + " Guild Experience");
		sendChatEvent("    Dec 16 2018:   " + getRandomNr() + " Guild Experience");
		sendChatEvent("    Dec 15 2018:   " + getRandomNr() + " Guild Experience");
		sendChatEvent("    Dec 14 2018:   " + getRandomNr() + " Guild Experience");
		sendChatEvent("    Dec 13 2018:   " + getRandomNr() + " Guild Experience");
	}

	public static void send_permission_denied() {
		sendChatEvent("You do not have permission to use this command!");
	}

	public static void send_guild_info() {
		sendChatEvent("                                   The Skys");
		sendChatEvent("");
		sendChatEvent("");
		sendChatEvent("Created: 2018-01-08 16:46 EST");
		sendChatEvent("Members: 110/125");
		sendChatEvent("Description: Wellcome to the guild");
		sendChatEvent("MOTD last edited by fexes at 2018-11-11 19:26 EST");
		sendChatEvent("");
		sendChatEvent("Guild Exp: 45,161,724  (#336)");
		sendChatEvent("Guild Level: 22 (39% to Level 23)");
		sendChatEvent("");
		sendChatEvent("    Today:           " + getRandomNr() + " Guild Experience");
		sendChatEvent("    Dec 18 2018:   " + getRandomNr() + " Guild Experience");
		sendChatEvent("    Dec 17 2018:   " + getRandomNr() + " Guild Experience");
		sendChatEvent("    Dec 16 2018:   " + getRandomNr() + " Guild Experience");
		sendChatEvent("    Dec 15 2018:   " + getRandomNr() + " Guild Experience");
		sendChatEvent("    Dec 14 2018:   " + getRandomNr() + " Guild Experience");
		sendChatEvent("    Dec 13 2018:   " + getRandomNr() + " Guild Experience");
	}

	// do "/hello" ;)
	public static void send_hello_command() {
		sendChatEvent("Why hello there.");
	}

	public static void send_must_guild_master() {
		sendChatEvent("You must be the Guild Master to use that command!");
	}

	private static void sendChatEvent(String message) {
		ChatComponentText chatText = new ChatComponentText(message);

		IChatComponent chatEvent = ForgeEventFactory.onClientChat((byte) 0, chatText);

		try {
			Thread.sleep(1);
			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(chatEvent);
		} catch (Exception ignored) {}
	}

	private static double getRandomNr() {
		return Math.floor(Math.random() * 20000);
	}

	private static String getRandomName() {
		return Double.toHexString(getRandomNr());
	}
}
