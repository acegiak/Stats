package com.nidefawl.Stats.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityDeathEvent;

public class StatsPlayerDeathByPlayerEvent extends org.bukkit.event.Event implements Cancellable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1834044792921833350L;
	private Player player;
	private Player killer;
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

	public StatsPlayerDeathByPlayerEvent(EntityDeathEvent event, Player player, Player killer) {
		super("StatsPlayerDeathByPlayerEvent");
		this.base = event;
		this.player = player;
		this.killer = killer;
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
	 * @param killer
	 *            the killer to set
	 */
	public void setKiller(Player killer) {
		this.killer = killer;
	}

	/**
	 * @return the base
	 */
	public EntityDeathEvent getBase() {
		return base;
	}

	/**
	 * @return the killer
	 */
	public Player getKiller() {
		return killer;
	}

}
