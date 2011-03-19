package com.nidefawl.Stats.event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;

public class StatsMobDeathByPlayerEvent extends org.bukkit.event.Event {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2158229946386582299L;
	private Player player;
	boolean isCancelled;
	EntityDeathEvent base;

	public StatsMobDeathByPlayerEvent(EntityDeathEvent event, Player player, Entity entity) {
		super("StatsMobDeathByPlayerEvent");
		this.base = event;
		this.player = player;
		isCancelled = false;
	}

	/**
	 * @return the entity
	 */
	public Entity getEntity() {
		return this.base.getEntity();
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

}
