package com.aidn5.hypeguild.models;

import java.util.HashMap;

/**
 * Holder holds the information of a member
 * 
 * @author aidn5
 *
 */
public class GuildMember implements Comparable<GuildMember> {

	public String username = null;
	public String uuid = null;

	public String guildName = null;
	public int joinedAt = -1;

	public String rank = "Member";
	public String tag = "";
	public int rankProirity = 0;
	public String tagColor = "DARK_AQUA";

	public int lastTimeOnline = -1;
	private double hypixelLevel = -1;

	public HashMap<Integer, Integer> coins = null;
	private int totalGuildExp = -1;

	/**
	 * This method returns the level of a player calculated by the current
	 * experience gathered. The result is a precise level of the player The value is
	 * not zero-indexed and represents the absolute visible level for the player.
	 * <p>
	 * <b>Credits:</b>
	 * https://github.com/HypixelDev/PublicAPI/blob/master/Java/src/main/java/net/hypixel/api/util/ILeveling.java
	 * </p>
	 * 
	 * @return player's level
	 */
	double getHypixelLevel() {
		final double REVERSE_PQ_PREFIX = -3.5;
		final double REVERSE_CONST = 12.25;
		final double GROWTH_DIVIDES_2 = 0.0008;

		if (this.hypixelLevel < 0) return 1;
		return Math.floor(1 + REVERSE_PQ_PREFIX + Math.sqrt(REVERSE_CONST + GROWTH_DIVIDES_2 * this.hypixelLevel));
	}

	public int getWeeklyGuildExp() {
		if (this.totalGuildExp > 0) return this.totalGuildExp;

		for (Integer value : this.coins.values()) {
			this.totalGuildExp += value;
		}

		return this.totalGuildExp;
	}

	@Override
	public int compareTo(GuildMember o2) {
		// First in rank priority
		if (this.rankProirity > o2.rankProirity) return -1;
		else if (this.rankProirity < o2.rankProirity) return 1;

		// If the same... who tag has
		if (!this.tag.isEmpty() && o2.tag.isEmpty()) return -1;
		else if (this.tag.isEmpty() && !o2.tag.isEmpty()) return 1;

		// See in name?!
		return this.rank.toLowerCase().compareTo(o2.rank.toLowerCase());
	}

	@Override
	public GuildMember clone() {
		GuildMember guildMember = new GuildMember();

		guildMember.username = this.username;
		guildMember.uuid = this.uuid;

		guildMember.guildName = this.guildName;
		guildMember.joinedAt = this.joinedAt;

		guildMember.rank = this.rank;
		guildMember.tag = this.tag;
		guildMember.rankProirity = this.rankProirity;
		guildMember.tagColor = this.tagColor;

		guildMember.lastTimeOnline = this.lastTimeOnline;
		guildMember.hypixelLevel = this.hypixelLevel;

		guildMember.coins = (HashMap<Integer, Integer>) this.coins.clone();

		return guildMember;
	}
}
