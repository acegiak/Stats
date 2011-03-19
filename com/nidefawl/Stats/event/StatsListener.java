package com.nidefawl.Stats.event;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

public class StatsListener extends CustomEventListener implements Listener {

	public StatsListener() {

	}

	@Override
	public void onCustomEvent(Event event) {
		if (event instanceof StatsMobDeathByPlayerEvent) {
			onStatsMobDeathByPlayerEvent((StatsMobDeathByPlayerEvent) event);
		} else if (event instanceof StatsPlayerDamagedPlayerEvent) {
			onStatsPlayerDamagedPlayerEvent((StatsPlayerDamagedPlayerEvent) event);
		} else if (event instanceof StatsPlayerDeathByEntityEvent) {
			onStatsPlayerDeathByEntityEvent((StatsPlayerDeathByEntityEvent) event);
		} else if (event instanceof StatsPlayerDeathByPlayerEvent) {
			onStatsPlayerDeathByPlayerEvent((StatsPlayerDeathByPlayerEvent) event);
		} else if (event instanceof StatsPlayerDeathByOtherEvent) {
			onStatsPlayerDeathByOtherEvent((StatsPlayerDeathByOtherEvent) event);
		} else if (event instanceof StatsPlayerMoveEvent) {
			onStatsPlayerMoveEvent((StatsPlayerMoveEvent) event);
		}
	}

	public void onStatsPlayerMoveEvent(StatsPlayerMoveEvent event) {

	}

	public void onStatsPlayerDeathByOtherEvent(StatsPlayerDeathByOtherEvent event) {

	}

	public void onStatsPlayerDeathByPlayerEvent(StatsPlayerDeathByPlayerEvent event) {

	}

	public void onStatsPlayerDeathByEntityEvent(StatsPlayerDeathByEntityEvent event) {

	}

	public void onStatsPlayerDamagedPlayerEvent(StatsPlayerDamagedPlayerEvent event) {

	}

	public void onStatsMobDeathByPlayerEvent(StatsMobDeathByPlayerEvent event) {
	}

}
