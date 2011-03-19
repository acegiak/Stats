package com.nidefawl.Stats.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityDeathEvent;

public class StatsPlayerDeathByOtherEvent extends org.bukkit.event.Event implements Cancellable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7138441428433873857L;
	private Player player;
	private String reason;
	boolean isCancelled;
	private EntityDeathEvent base;

	@Override
	public boolean isCancelled() {
		return this.isCancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		isCancelled = arg0;
	}

	public StatsPlayerDeathByOtherEvent(EntityDeathEvent event, Player player, String reason) {
		super("StatsPlayerDeathByOtherEvent");
		this.base = event;
		this.player = player;
		this.reason = reason;
		isCancelled = false;
	}

	/**
	 * @param player
	 *            the player to set
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
	 * @param reason
	 *            the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * @return the base
	 */
	public EntityDeathEvent getBase() {
		return base;
	}

	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}

}
