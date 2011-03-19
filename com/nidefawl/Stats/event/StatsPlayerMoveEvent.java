package com.nidefawl.Stats.event;

public class StatsPlayerMoveEvent extends org.bukkit.event.Event {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4195026324747114268L;
	private String player;
	private int distance;

	public StatsPlayerMoveEvent(String player, int distance) {
		super("StatsPlayerMoveEvent");
		this.player = player;
		this.distance = distance;
	}

	/**
	 * @param player
	 *            the player to set
	 */
	public void setPlayer(String player) {
		this.player = player;
	}

	/**
	 * @return the player
	 */
	public String getPlayer() {
		return player;
	}

	/**
	 * @param distance
	 *            the distance to set
	 */
	public void setDistance(int distance) {
		this.distance = distance;
	}

	/**
	 * @return the distance
	 */
	public int getDistance() {
		return distance;
	}

}
