package com.aidn5.hypeguild.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.aidn5.hypeguild.HypeGuild;
import com.aidn5.hypeguild.fetcher.MembersDetails;
import com.aidn5.hypeguild.fetcher.MembersDetails.Response;
import com.aidn5.hypeguild.gui.ListViewer;
import com.aidn5.hypeguild.models.GuildMember;

import net.minecraft.util.EnumChatFormatting;

public class ByGuildExp implements Runnable {
	public boolean forceRefresh = false;

	@Override
	public void run() {
		MembersDetails membersDetails = new MembersDetails();
		membersDetails.forceRefresh = this.forceRefresh;

		membersDetails.response = new Response() {
			@Override
			public void onFinished(List<GuildMember> guildMembers) {
				doAfterGetMembers(guildMembers);
			}
		};

		membersDetails.run();
	}

	private void doAfterGetMembers(List<GuildMember> guildMembers) {
		TreeMap<String, Integer> members = new TreeMap<String, Integer>();

		List<String> list = new ArrayList<String>();

		for (GuildMember guildMember : guildMembers) {
			String username = guildMember.username;
			Integer totalExp = 0;

			for (Integer value : guildMember.coins.values()) {
				totalExp += value;
			}

			members.put(username, totalExp);
		}

		for (Entry<String, Integer> member : members.entrySet()) {
			String exp = EnumChatFormatting.BOLD + "" + EnumChatFormatting.GOLD + member.getValue();
			String memberName = EnumChatFormatting.WHITE + member.getKey();
			list.add(exp + "" + EnumChatFormatting.RESET + memberName);
		}

		HypeGuild.instance.guiToDisplay = new ListViewer(list);
	}
}
