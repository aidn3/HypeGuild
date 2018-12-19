package com.aidn5.hypeguild.fetcher;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aidn5.hypeguild.gui.ProgressViewer;
import com.aidn5.hypeguild.models.GuildMember;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MembersDetails extends Fetcher implements Runnable {
	private static Pattern onEnd = Pattern.compile("^Why hello there");
	private static Pattern noPerm = Pattern.compile("^You do not have permission to");
	private static Pattern startCoinsMember = Pattern.compile("^Guild Exp Contributions:");
	private static Pattern getCoinsMember = Pattern
			.compile("^(Today|[a-zA-Z]{1,100} [0-9]{0,3} [0-9]{1,5}):[ ]{1,100}([0-9]{1,30})");

	/**
	 * Ignore the cache
	 */
	public boolean forceRefresh = false;

	/**
	 * Do it silently and do not cancel
	 */
	public boolean showWaitingGui = true;

	/**
	 * Callback for onFinished(), onCancelled()
	 */
	public Response response;

	private List<GuildMember> guildMembers;

	private StringBuilder memberDetails;
	private ProgressViewer progressViewer;

	private boolean stop = false;
	// TODO: Change the way how currentProgress works
	private int currentProgress = -1;

	@Override
	public void run() {
		showMessage("Listing all the members...", "Hi :)");

		if (this.showWaitingGui) initGui();

		guildMembers = getGuildMembers(this.forceRefresh);

		if (guildMembers == null) {
			cancel(null);

			// Reason for the Cancellation Message
			// has already been sent in getGuildMembers()
			return;
		}

		sortMembers(guildMembers);

		MinecraftForge.EVENT_BUS.register(this);
		doCycle();
	}

	private void doCycle() {
		GuildMember guildMember = this.guildMembers.get(this.currentProgress);
		guildMember.coins = getCoins(this.memberDetails.toString(), guildMember);

		if (this.currentProgress + 1 == this.guildMembers.size()) {
			cancel(null);
			finish();
			return;
		}

		this.currentProgress++;
		this.progressViewer.currentProgress = this.currentProgress;
		this.memberDetails = new StringBuilder();

		ChatComponentText iText = new ChatComponentText("/g member " + this.guildMembers.get(currentProgress).username);
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(iText);

		ChatComponentText endCall = new ChatComponentText("/hello");
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(endCall);
	}

	private void finish() {
		if (this.response != null) {
			this.response.onFinished(this.guildMembers);
		}
	}

	/**
	 * stop/cancel the current progression
	 */
	public void cancel(String reason) {
		this.stop = true;
		MinecraftForge.EVENT_BUS.unregister(this);

		if (reason != null && !reason.isEmpty()) {
			String message = EnumChatFormatting.RED + reason;
			String toolTip = "ouch 7u7";
			showMessage(message, toolTip);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void onPlayerChatReceive(ClientChatReceivedEvent event) {
		if (event.type != 1) return;

		event.setCanceled(true); // We do not want to spam the user with every member info

		String message = event.message.getUnformattedText().trim();

		Matcher isTheEnd = onEnd.matcher(message);
		if (isTheEnd.find()) {
			doCycle();
			return;
		}

		Matcher noPerm = this.noPerm.matcher(message);
		if (noPerm.find()) {
			cancel("You do not have permissions... :(");
			return;
		}

		this.memberDetails.append(message);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onWorldChange(WorldEvent.Load event) {
		cancel("You changed the world!");
	}

	private void initGui() {
		this.progressViewer = new ProgressViewer();
		this.progressViewer.total = guildMembers.size();
		this.progressViewer.currentProgress = currentProgress;
		this.progressViewer.onCancel = new Runnable() {
			@Override
			public void run() {
				cancel("Gui Cancelled");
			}
		};
	}

	public interface Response {
		public void onFinished(List<GuildMember> guildMembers);
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
					Integer exp = Integer.valueOf(matcher.group(1));
					Integer date = (int) ((System.currentTimeMillis() - (day * count)) / 1000L);
					coins.put(date, exp);
				}
				count++;
			}
		}

		return coins;

	};
}
