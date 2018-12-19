package com.aidn5.hypeguild.commands;

import java.util.ArrayList;
import java.util.List;

import com.aidn5.hypeguild.HypeGuild;
import com.aidn5.hypeguild.ModConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class Command extends CommandBase {
	private static final String[] commandName = new String[] { "/" + ModConfig.MODID };

	private static final EnumChatFormatting primary = EnumChatFormatting.WHITE;
	private static final EnumChatFormatting neutral = EnumChatFormatting.GRAY;
	private static final EnumChatFormatting secondary = EnumChatFormatting.YELLOW;

	@Override
	public String getCommandName() {
		return commandName[0];
	}

	@Override
	public void processCommand(ICommandSender sender, final String[] args) throws CommandException {
		for (int i = 0; i < args.length; i++) {
			args[i] = args[i].toLowerCase();
		}

		if (args.length == 0) {
			getCommandUsage(sender);
			return;
		}

		if (args[0].equals("settings")) {
			// new Thread(new openGui()).start();
			// HypeGuild.instance.guiToDisplay = new GuiHandler();
			return;

		} else if (args[0].equals("listall")) {
			ListAll listAll = new ListAll();
			if (args.length > 1 && args[1].equals("forcerefresh")) {
				listAll.forceRefresh = true;
			}

			listAll.run();
			return;

		} else if (args[0].equals("byguildexp")) {
			ByGuildExp byGuildExp = new ByGuildExp();

			if (args.length > 1 && args[1].equals("forcerefresh")) {
				byGuildExp.forceRefresh = true;
			}

			byGuildExp.run();

			return;

		} else if (args[0].equals("byhypelevel")) {
			return;

		} else if (args[0].equals("bylastonline")) {
			return;

		} else if (args[0].equals("setapikey")) {
			if (args.length == 1) Minecraft.getMinecraft().thePlayer.sendChatMessage("/api");

			else if (args[1].length() != 36) {
				sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Invaild api"));
			} else {
				HypeGuild.instance.settings.setProperty("api", args[1]);
				HypeGuild.instance.settings.SaveUserSettings();
				sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "API has been saved!"));
			}

			return;
		}

		// Show the usage when non of the IF conditions passed
		getCommandUsage(sender);
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		List<String> completions = new ArrayList();

		if (args.length == 1) {
			String[] list = new String[] { "listAll", "byGuildExp", "byHypeLevel", "byLastOnline", "settings",
					"setApiKey" };

			for (String item : list) {
				if (item.toLowerCase().startsWith(args[0].toLowerCase())) completions.add(item);
			}

		} else if (args.length == 2) {
			String[] list = new String[] { "byGuildExp", "byHypeLevel", "byLastOnline", "listAll" };

			String a = EnumChatFormatting.DARK_PURPLE + "";
			for (String item : list) {
				if (item.toLowerCase().equals(args[0].toLowerCase())) {
					completions.add("forceRefresh");
				}
			}
		}

		return completions;
	}

	/**
	 * Let un-OPPED users use the command
	 */
	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		ChatComponentText usage = new ChatComponentText("");
		usage.appendSibling(new ChatComponentText(neutral + "-=-" + primary + commandName[0] + neutral + "-=-\n"));

		usage.appendSibling(createUageCommand("listAll", "List all members of the guild"));
		usage.appendSibling(createUageCommand("byGuildExp", "List players sorted by their production"));
		usage.appendSibling(createUageCommand("byHypeLevel", "List players sorted by their hypixel level"));
		usage.appendSibling(createUageCommand("byLastOnline", "List players sorted by last time they were online"));
		usage.appendSibling(createUageCommand("settings", "Show GUI settings for " + ModConfig.NAME));
		usage.appendSibling(createUageCommand("setApiKey", "Set Hypixel's Api Key"));

		sender.addChatMessage(usage);
		return null;
	}

	/**
	 * return {@link IChatComponent} with the styled string "[command]" and tool-tip
	 * says the message
	 * 
	 * @param String
	 *            command
	 * @param String
	 *            message
	 * @return {@link IChatComponent}
	 */
	private IChatComponent createUageCommand(String command, String message) {
		// Create tool-tip text for on hovering event
		ChatComponentText hoverText = new ChatComponentText("");

		// Add the message to the hover text
		ChatComponentText msgComponent = new ChatComponentText(message);
		msgComponent.setChatStyle(new ChatStyle().setColor(neutral));
		hoverText.appendSibling(msgComponent);
		hoverText.appendText("\n");

		hoverText.appendSibling(new ChatComponentText(EnumChatFormatting.OBFUSCATED + "A"));
		hoverText.appendSibling(new ChatComponentText("Click to run!"));
		hoverText.appendSibling(new ChatComponentText(EnumChatFormatting.OBFUSCATED + "X"));

		;
		// Create style for the finalMessage
		ChatStyle chatStyle = new ChatStyle();

		// Set the tool-tip event
		chatStyle.setChatHoverEvent(new HoverEvent(net.minecraft.event.HoverEvent.Action.SHOW_TEXT, hoverText));

		// Set on click event. Run the command on clicking
		chatStyle.setChatClickEvent(new ClickEvent(Action.RUN_COMMAND, "/" + commandName[0] + " " + command));

		;
		// Create the text to be viewed
		ChatComponentText commandPart1 = new ChatComponentText("/" + commandName[0] + " ");
		commandPart1.setChatStyle(new ChatStyle().setColor(primary));

		ChatComponentText commandPart2 = new ChatComponentText(command);
		commandPart2.setChatStyle(new ChatStyle().setColor(secondary));

		;
		// Final message to be returned
		ChatComponentText finalMessage = new ChatComponentText("");
		finalMessage.appendSibling(commandPart1);
		finalMessage.appendSibling(commandPart2);
		finalMessage.appendText("\n");

		// Set the style for the message
		finalMessage.setChatStyle(chatStyle);

		return finalMessage;
	}
}
