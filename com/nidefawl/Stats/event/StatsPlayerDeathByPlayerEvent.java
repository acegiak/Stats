package com.nidefawl.Stats.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class StatsPlayerDeathByPlayerEvent extends org.bukkit.event.Event implements Cancellable  {
	private Player player;
	private Player killer;
	boolean isCancelled;

	@Override
	public boolean isCancelled() {
		return this.isCancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		isCancelled = arg0;
	}

	public StatsPlayerDeathByPlayerEvent(Player player, Player killer) {
		super("StatsPlayerDamageEvent");
		this.player = player;
		this.killer = killer;
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
	 * @param killer the killer to set
	 */
	public void setKiller(Player killer) {
		this.killer = killer;
	}

	/**
	 * @return the killer
	 */
	public Player getKiller() {
		return killer;
	}

}
