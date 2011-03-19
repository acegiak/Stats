package com.nidefawl.Stats;

import org.bukkit.entity.Player;
import org.bukkit.event.vehicle.*;

/**
 * Listener for vehicle events.
 * 
 * @author sk89q
 */
public class StatsVehicleListener extends VehicleListener {
	private Stats plugin;

	public StatsVehicleListener(Stats plugin) {
		this.plugin = plugin;
	}

	/**
	 * Called when a vehicle is created by a player. This hook will be called
	 * for all vehicles created.
	 * 
	 * @param event
	 */
	@Override
	public void onVehicleCreate(VehicleCreateEvent event) {
	}

	/**
	 * Called when a vehicle is damaged by the player.
	 * 
	 * @param event
	 */
	@Override
	public void onVehicleDamage(VehicleDamageEvent event) {
	}

	/**
	 * Called when a vehicle collides with a block.
	 * 
	 * @param event
	 */
	@Override
	public void onVehicleBlockCollision(VehicleBlockCollisionEvent event) {
	}

	/**
	 * Called when a vehicle collides with an entity.
	 * 
	 * @param event
	 */
	@Override
	public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
	}

	/**
	 * Called when an entity enters a vehicle.
	 * 
	 * @param event
	 */
	@Override
	public void onVehicleEnter(VehicleEnterEvent event) {
		if (!(event.getEntered() instanceof Player))
			return;
		plugin.updateVehicleEnter(((Player) event.getEntered()).getName(), event.getVehicle());
	}

	/**
	 * Called when an entity exits a vehicle.
	 * 
	 * @param event
	 */
	@Override
	public void onVehicleExit(VehicleExitEvent event) {
	}

	/**
	 * Called when an vehicle moves.
	 * 
	 * @param event
	 */
	@Override
	public void onVehicleMove(VehicleMoveEvent event) {
		if (event.getVehicle() == null)
			return;
		if (!(event.getVehicle().getPassenger() instanceof Player))
			return;
		plugin.updateVehicleMove(((Player) event.getVehicle().getPassenger()).getName(), event.getVehicle(), event.getFrom(), event.getTo());
	}
}