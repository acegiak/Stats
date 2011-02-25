package com.nidefawl.Stats.event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class StatsPlayerDeathByEntityEvent extends org.bukkit.event.Event implements Cancellable {
	private Player player;
	private Entity entity;
	boolean isCancelled;

	@Override
	public boolean isCancelled() {
		return this.isCancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		isCancelled = arg0;
	}

	public StatsPlayerDeathByEntityEvent(Player player, Entity entity) {
		super("StatsPlayerDamageEvent");
		this.player = player;
		this.entity = entity;
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


}
