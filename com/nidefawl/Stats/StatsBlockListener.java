package com.nidefawl.Stats;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SignChangeEvent;

public class StatsBlockListener extends BlockListener {
	private Stats plugin;

	public StatsBlockListener(Stats plugin) {
		this.plugin = plugin;
	}

	/**
	 * Called when a block is damaged (or broken)
	 * 
	 * @param event
	 *            Relevant event details
	 */
	@Override
	public void onBlockDamage(BlockDamageEvent event) {
	}

	/**
	 * Called when a sign is changed
	 * 
	 * @param event
	 *            Relevant event details
	 */
	@Override
	public void onSignChange(SignChangeEvent event) {
	}

	/**
	 * Called when a block is destroyed from burning
	 * 
	 * @param event
	 *            Relevant event details
	 */
	@Override
	public void onBlockBurn(BlockBurnEvent event) {
	}

	/**
	 * Called when a block is destroyed by a player.
	 * 
	 * @param event
	 *            Relevant event details
	 */
	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;
		if (!(event.getPlayer() instanceof Player))
			return;
		plugin.updateStat(event.getPlayer(), "blockdestroy", event.getBlock(), true);
		plugin.updateStat(event.getPlayer(), "totalblockdestroy", true);
	}

	/**
	 * Called when we try to place a block, to see if we can build it
	 */
	@Override
	public void onBlockCanBuild(BlockCanBuildEvent event) {
	}


	/**
	 * Called when a block gets ignited
	 * 
	 * @param event
	 *            Relevant event details
	 */
	@Override
	public void onBlockIgnite(BlockIgniteEvent event) {
		if (event.isCancelled())
			return;
		if (!(event.getPlayer() instanceof Player))
			return;
		plugin.updateStat(event.getPlayer(), "lighter", true);

	}

	/**
	 * Called when block physics occurs
	 * 
	 * @param event
	 *            Relevant event details
	 */
	@Override
	public void onBlockPhysics(BlockPhysicsEvent event) {
	}

	/**
	 * Called when a player places a block
	 * 
	 * @param event
	 *            Relevant event details
	 */
	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled())
			return;
		if (!(event.getPlayer() instanceof Player))
			return;
		plugin.updateStat(event.getPlayer(), "blockcreate", event.getBlockPlaced(), true);
		plugin.updateStat(event.getPlayer(), "totalblockcreate", true);
	}




	/**
	 * Called when redstone changes From: the source of the redstone change To:
	 * The redstone dust that changed
	 * 
	 * @param event
	 *            Relevant event details
	 */
	@Override
	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
	}

	/**
	 * Called when leaves are decaying naturally
	 * 
	 * @param event
	 *            Relevant event details
	 */
	@Override
	public void onLeavesDecay(LeavesDecayEvent event) {
	}

}