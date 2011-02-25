package com.nidefawl.Stats.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class StatsPlayerDeathByOtherEvent extends org.bukkit.event.Event implements Cancellable {
	private Player player;
	private String reason;
	boolean isCancelled;

	@Override
	public boolean isCancelled() {
		return this.isCancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		isCancelled = arg0;
	}

	public StatsPlayerDeathByOtherEvent(Player player, String reason) {
		super("StatsPlayerDamageEvent");
		this.player = player;
		this.reason = reason;
		isCancelled=false;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}

}
