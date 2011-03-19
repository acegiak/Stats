package com.nidefawl.Stats.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class StatsPlayerDamagedPlayerEvent extends org.bukkit.event.Event implements Cancellable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -380510384968697307L;
	private Player player;
	private Player damaged;
	private int damage;
	boolean isCancelled;

	@Override
	public boolean isCancelled() {
		return this.isCancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		isCancelled = arg0;
	}

	public StatsPlayerDamagedPlayerEvent(Player player, Player damaged, int damage) {
		super("StatsPlayerDamagedPlayerEvent");
		this.player = player;
		this.damaged = damaged;
		this.damage = damage;
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
	 * @param damage
	 *            the damage to set
	 */
	public void setDamage(int damage) {
		this.damage = damage;
	}

	/**
	 * @return the damage
	 */
	public int getDamage() {
		return damage;
	}

	/**
	 * @param damaged
	 *            the damaged to set
	 */
	public void setDamagedPlayer(Player damaged) {
		this.damaged = damaged;
	}

	/**
	 * @return the damaged
	 */
	public Player getDamagedPlayer() {
		return damaged;
	}

}
