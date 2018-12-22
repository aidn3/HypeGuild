package com.aidn5.hypeguild.commands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.aidn5.hypeguild.HypeGuild;
import com.aidn5.hypeguild.fetcher.MembersCoins;
import com.aidn5.hypeguild.gui.ListViewer;
import com.aidn5.hypeguild.gui.ProgressViewer;
import com.aidn5.hypeguild.models.GuildMember;
import com.aidn5.hypeguild.util.MCUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

class ByGuildExp extends MembersCoins implements Runnable {

	public boolean forceRefresh = false;

	private ProgressViewer progressViewer;

	@Override
	public void run() {
		// TODO: Add "!" in the production! To run it on hypixel network
		if (HypeGuild.instance.onHypixel) {
			onCancel("Are you sure you are on hypixel network?", "tip: connect to mc.hypixel.net ;)");
			return;
		}

		this.progressViewer = new ProgressViewer();
		this.progressViewer.onCancel = new Runnable() {
			@Override
			public void run() {
				cancel();
				MCUtil.showMessage(EnumChatFormatting.RED + "Gui has been cancelled D:", "Next time: Do not move ;)");
			}
		};

		MCUtil.showMessage("Listing all the members...", "Hi :)");

		HypeGuild.instance.guiToDisplay = this.progressViewer;

		getMembersCoins(this.forceRefresh);
	}

	@Override
	protected void onFinish(List<GuildMember> guildMembers) {
		this.progressViewer.onCancel = null;

		guildMembers.sort(new Comparator<GuildMember>() {
			@Override
			public int compare(GuildMember o1, GuildMember o2) {
				if (o1.getWeeklyGuildExp() > o2.getWeeklyGuildExp()) return -1;
				else if (o1.getWeeklyGuildExp() < o2.getWeeklyGuildExp()) return 1;
				return o1.compareTo(o2);
			}
		});

		List<String> list = new ArrayList<String>();

		int count = 1;
		String ownerUuid = Minecraft.getMinecraft().getSession().getPlayerID();
		String ownerName = Minecraft.getMinecraft().getSession().getUsername();

		for (GuildMember guildMember : guildMembers) {
			String name = (guildMember.username != null) ? guildMember.username : guildMember.uuid;
			String color;

			if (guildMember.rank.equals("guildmaster")) {
				color = EnumChatFormatting.BOLD + "" + EnumChatFormatting.RED + "";

			} else if (guildMember.username.equals(ownerName) || guildMember.uuid.contains(ownerUuid)) {
				color = EnumChatFormatting.BOLD + "" + EnumChatFormatting.AQUA + "";

			} else {
				color = EnumChatFormatting.WHITE + "";
			}

			String exp = EnumChatFormatting.BOLD + "" + EnumChatFormatting.GOLD + guildMember.getWeeklyGuildExp();
			String memberName = color + name;
			String countNr = EnumChatFormatting.WHITE + "" + count + ". ";
			list.add(countNr + "" + exp + " " + EnumChatFormatting.RESET + memberName);
			count++;
		}

		HypeGuild.instance.guiToDisplay = new ListViewer(list);
	}

	@Override
	protected void onUpdate(int total, int currentProgress) {
		progressViewer.total = total;
		progressViewer.currentProgress = currentProgress;
	}

	@Override
	protected void onCancel(String reason, String toolTip) {
		if (reason != null && !reason.isEmpty()) {
			MCUtil.showMessage(reason, toolTip);
		}
	}
}
