package com.nidefawl.Stats;

import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftItem;
import org.bukkit.event.player.*;

import com.nidefawl.Stats.datasource.PlayerStat;

public class StatsPlayerListener extends PlayerListener {
	protected Stats plugin;

	public StatsPlayerListener(Stats plugin) {
		this.plugin = plugin;
	}

	/**
	 * Called when a player leaves a server
	 * 
	 * @param event
	 *            Relevant event details
	 */
	@Override
	public void onPlayerQuit(PlayerEvent event) {
		plugin.logout(event.getPlayer().getName());
		plugin.unload(event.getPlayer().getName());

	}

	/**
	 * Called when a player sends a chat message
	 * 
	 * @param event
	 *            Relevant event details
	 */
	@Override
	public void onPlayerChat(PlayerChatEvent event) {
		if (event.isCancelled())
			return;
		plugin.updateStat(event.getPlayer(), "chat", true);
		plugin.updateStat(event.getPlayer(), "chatletters", event.getMessage().length(), true);
	}

	/**
	 * Called when a player attempts to use a command
	 * 
	 * @param event
	 *            Relevant event details
	 */
	@Override
	public void onPlayerCommandPreprocess(PlayerChatEvent event) {
		if (event.isCancelled())
			return;
		plugin.updateStat(event.getPlayer(), "command", true);
	}

	/**
	 * Called when a player attempts to move location in a world
	 * 
	 * @param event
	 *            Relevant event details
	 */
	@Override
	public void onPlayerMove(PlayerMoveEvent event) {
		if (event.isCancelled())
			return;
		plugin.updateMove(event.getPlayer().getName(), event.getFrom(), event.getTo());

	}

	/**
	 * Called when a player attempts to teleport to a new location in a world
	 * 
	 * @param event
	 *            Relevant event details
	 */
	@Override
	public void onPlayerTeleport(PlayerMoveEvent event) {
		if (event.isCancelled())
			return;
		if (event.getTo().equals(event.getPlayer().getWorld().getSpawnLocation()))
			return;
		PlayerStat ps = plugin.stats.get(event.getPlayer().getName());
		if (ps == null)
			return;
		if (ps.skipTeleports > 0) {
			ps.skipTeleports--;
			return;
		}
		plugin.updateStat(event.getPlayer(), "teleport", false);
	}

	/**
	 * Called when a player uses an item
	 * 
	 * @param event
	 *            Relevant event details
	 */
	@Override
	public void onPlayerItem(PlayerItemEvent event) {
		if (event.isCancelled() || event.getBlockFace() == null)
			return;
		switch (event.getMaterial()) {
		case LAVA_BUCKET:
		case WATER_BUCKET:
		case SIGN:
		case BUCKET:
		case CAKE_BLOCK:
		case FLINT_AND_STEEL:
		case TNT:
			plugin.updateStat(event.getPlayer(), "itemuse", plugin.getItems().getItem(event.getMaterial().getId()), 1, false);
			break;
		default:
			break;
		}

	}

	/**
	 * Called when a player attempts to log in to the server
	 * 
	 * @param event
	 *            Relevant event details
	 */
	@Override
	public void onPlayerJoin(PlayerEvent event) {
		plugin.load(event.getPlayer());
		plugin.login(event.getPlayer());
	}

	/**
	 * Called when a player plays an animation, such as an arm swing
	 * 
	 * @param event
	 *            Relevant event details
	 */
	@Override
	public void onPlayerAnimation(PlayerAnimationEvent event) {
		if (event.getAnimationType() == PlayerAnimationType.ARM_SWING && event.getPlayer().getItemInHand().getType() == Material.AIR) {
			plugin.updateStat(event.getPlayer(), "armswing", false);
		}
	}

	/**
	 * Called when a player throws an egg and it might hatch
	 * 
	 * @param event
	 *            Relevant event details
	 */
	@Override
	public void onPlayerEggThrow(PlayerEggThrowEvent event) {
		plugin.updateStat(event.getPlayer(), "eggthrow", false);
	}

	/**
	 * Called when a player drops an item from their inventory
	 * 
	 * @param event
	 *            Relevant event details
	 */
	@Override
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (event.isCancelled())
			return;
		if (event.getItemDrop() == null)
			return;
		if (event.getItemDrop() instanceof CraftItem) {
			if (((CraftItem) event.getItemDrop()).getItemStack() == null)
				return;
			plugin.updateStat(event.getPlayer(), "itemdrop", plugin.getItems().getItem(((CraftItem) event.getItemDrop()).getItemStack().getTypeId()), 1, false);
		}

	}

	/**
	 * Called when a player gets kicked from the server
	 * 
	 * @param event
	 *            Relevant event details
	 */
	@Override
	public void onPlayerKick(PlayerKickEvent event) {
		if (event.isCancelled())
			return;
		plugin.updateStat(event.getPlayer(), "kick", false);
	}

	/**
	 * Called when a player respawns
	 * 
	 * @param event
	 *            Relevant event details
	 */
	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		plugin.updateStat(event.getPlayer(), "respawn", false);
	}

	/**
	 * Called when a player attempts to log in to the server
	 * 
	 * @param event
	 *            Relevant event details
	 */
	@Override
	public void onPlayerLogin(PlayerLoginEvent event) {
	}

	/**
	 * Called when a player picks an item up off the ground
	 * 
	 * @param event
	 *            Relevant event details
	 */
	@Override
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (event.isCancelled())
			return;
		if (event.getItem() == null)
			return;
		if (event.getItem() instanceof CraftItem) {
			if (((CraftItem) event.getItem()).getItemStack() == null)
				return;
			plugin.updateStat(event.getPlayer(), "itempickup", plugin.getItems().getItem(((CraftItem) event.getItem()).getItemStack().getTypeId()), 1, false);
		}
	}

	/**
	 * Called when a player opens an inventory
	 * 
	 * @param event
	 *            Relevant event details
	 */
	@Override
	public void onInventoryOpen(PlayerInventoryEvent event) {
	}

	/**
	 * Called when a player changes their held item
	 * 
	 * @param event
	 *            Relevant event details
	 */
	@Override
	public void onItemHeldChange(PlayerItemHeldEvent event) {
	}

	/**
	 * Called when a player toggles sneak mode
	 * 
	 * @param event
	 *            Relevant event details
	 */
	@Override
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
	}
}
