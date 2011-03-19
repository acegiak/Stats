package com.nidefawl.Stats.event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityDeathEvent;

public class StatsPlayerDeathByEntityEvent extends org.bukkit.event.Event implements Cancellable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7763150082128350151L;
	private Player player;
	private Entity entity;
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

	public StatsPlayerDeathByEntityEvent(EntityDeathEvent event, Player player, Entity entity) {

		super("StatsPlayerDeathByEntityEvent");
		this.base = event;
		this.player = player;
		this.entity = entity;
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
	 * @param entity
	 *            the entity to set
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
	 * @return the base
	 */
	public EntityDeathEvent getBase() {
		return base;
	}

}
