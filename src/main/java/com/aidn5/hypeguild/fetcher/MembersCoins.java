package com.aidn5.hypeguild.fetcher;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aidn5.hypeguild.models.GuildMember;
import com.aidn5.hypeguild.util.MCUtil;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Locks the chat, sends commands (e.g. /g member [name]), fetch the
 * data/members and returns them
 * 
 * @author aidn5
 *
 */
public abstract class MembersCoins extends Fetcher {

	private static final Pattern onEnd = Pattern.compile("^Why hello there");
	private static final Pattern noPerm = Pattern.compile("^You do not have permission to");
	private static final Pattern startCoinsMember = Pattern.compile("^Guild Exp Contributions:");
	private static final Pattern getCoinsMember = Pattern
			.compile("^(Today|[a-zA-Z]{1,100} [0-9]{0,3} [0-9]{1,5}):[ ]{1,100}([0-9]{1,30})");

	private int currentProgress = 0;
	private int total = 0;

	private List<GuildMember> guildMembers;

	private StringBuilder memberDetails;

	private boolean stop = false;
	private String stopReason = null;
	private String stopToolTip = null;

	/**
	 * 
	 * @param forceRefresh
	 *            Ignore the cache
	 */
	public void getMembersCoins(boolean forceRefresh) {
		guildMembers = getGuildMembers(forceRefresh);

		if (guildMembers == null) {
			if (errorMessage != null) cancel(errorMessage, errorToolTip);
			return;
		}

		loadUsernames(guildMembers);
		guildMembers.sort(null);
		this.total = guildMembers.size();

		MinecraftForge.EVENT_BUS.register(this);
		doCycle();
	}

	/**
	 * stop/cancel the current progression
	 */
	public void cancel() {
		cancel(null, null);
	}

	private void cancel(String reason, String toolTip) {
		this.stop = true;

		this.stopReason = reason;
		this.stopToolTip = toolTip;
	}

	private void doCycle() {
		if (this.memberDetails != null) {
			GuildMember guildMember = this.guildMembers.get(this.currentProgress - 1);
			guildMember.coins = getCoins(this.memberDetails.toString(), guildMember);
		}

		if (this.currentProgress == this.guildMembers.size()) {
			MinecraftForge.EVENT_BUS.unregister(this);
			finish();
			return;
		}

		if (this.stop) {
			MinecraftForge.EVENT_BUS.unregister(this);

			if (this.stopReason != null && !this.stopReason.isEmpty()) {
				onCancel(this.stopReason, this.stopToolTip);
			}

			return;
		}
		this.currentProgress++;
		this.memberDetails = new StringBuilder();
		onUpdate(this.total, this.currentProgress);

		MCUtil.sendChatMessage("/g member " + this.guildMembers.get(currentProgress - 1).username);
		MCUtil.sendChatMessage("/hello");
	}

	private void finish() {
		cancel(null, null);
		onFinish(this.guildMembers);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void onPlayerChatReceive(ClientChatReceivedEvent event) {
		if (event.type != 0) return;

		event.setCanceled(true); // We do not want to spam the user with every member stat

		String message = event.message.getUnformattedText().trim();

		Matcher isTheEnd = onEnd.matcher(message);
		if (isTheEnd.find()) {
			doCycle();
			return;
		}

		Matcher noPerm = this.noPerm.matcher(message);
		if (noPerm.find()) {
			cancel("You do not have permissions... :(", null);
			return;
		}

		this.memberDetails.append(message).append("\r\n");
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onWorldChange(WorldEvent.Load event) {
		cancel("You changed the world!", null);
	}

	private HashMap<Integer, Integer> getCoins(String data, GuildMember guildMember) {
		HashMap<Integer, Integer> coins = new HashMap<Integer, Integer>();

		String[] lines = data.split("[\\r\\n]+");

		final int day = (1000 * 60 * 60 * 24) + 1; // 1 is just in case :P
		boolean start = false;// when the data start to fetch;
		int count = 0;

		for (int i = 0; i < lines.length; i++) {
			lines[i] = lines[i].trim();

			if (startCoinsMember.matcher(lines[i]).find()) start = true;

			if (start) {
				Matcher matcher = getCoinsMember.matcher(lines[i]);
				if (matcher.find()) {
					Integer exp = Integer.valueOf(matcher.group(2));
					Integer date = (int) ((System.currentTimeMillis() - (day * count)) / 1000L);
					coins.put(date, exp);
				}
				count++;
			}
		}
		return coins;
	};

	protected abstract void onFinish(List<GuildMember> guildMembers);

	protected abstract void onUpdate(int total, int currentProgress);

	protected abstract void onCancel(String reason, String toolTip);
}
