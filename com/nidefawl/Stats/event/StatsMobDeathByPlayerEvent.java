package com.nidefawl.Stats.event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class StatsMobDeathByPlayerEvent extends org.bukkit.event.Event implements Cancellable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2158229946386582299L;
	private Entity entity;
	private Player player;
	boolean isCancelled;

	@Override
	public boolean isCancelled() {
		return this.isCancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		isCancelled = arg0;
	}

	public StatsMobDeathByPlayerEvent(Player player, Entity entity) {
		super("StatsPlayerDamageEvent");
		this.entity = entity;
		this.player = player;
		isCancelled=false;
	}

	/**
	 * @param entity the entity to set
	 */
	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	/**
	 * @return the entity
	 */
	public Entity getEntity() {
		return entity;
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
	
}
