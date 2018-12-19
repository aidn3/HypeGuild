package com.aidn5.hypeguild.commands;

import java.util.ArrayList;
import java.util.List;

import com.aidn5.hypeguild.HypeGuild;
import com.aidn5.hypeguild.fetcher.Fetcher;
import com.aidn5.hypeguild.gui.ListViewer;
import com.aidn5.hypeguild.models.GuildMember;

import net.minecraft.util.EnumChatFormatting;

public class ListAll extends Fetcher implements Runnable {
	public boolean forceRefresh = false;

	@Override
	public void run() {
		showMessage("Listing all the members...", "Hi :)");

		List<GuildMember> guildMembers = getGuildMembers(this.forceRefresh);
		if (guildMembers == null) return; // Message has already been shown in getGuildMembers()

		sortMembers(guildMembers);

		List<String> list = new ArrayList<String>();

		for (GuildMember guildMember : guildMembers) {
			String tag = (guildMember.tag.equals("")) ? ""
					: EnumChatFormatting.valueOf(guildMember.tagColor) + "[" + guildMember.tag + "] ";
			String rank = EnumChatFormatting.YELLOW + guildMember.rank + " ";

			String username = HypeGuild.instance.ignUuidResolver.getUsername(guildMember.uuid);
			if (username != null) {
				list.add(rank + tag + EnumChatFormatting.WHITE + username);
			} else {
				list.add(rank + tag + EnumChatFormatting.WHITE + guildMember.uuid);
			}

		}

		// Save new IGN/UUID if there is
		HypeGuild.instance.ignUuidResolver.save();

		HypeGuild.instance.guiToDisplay = new ListViewer(list);

	}
}
