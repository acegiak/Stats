package com.nidefawl.Stats.Permissions;

import org.bukkit.entity.Player;

public class defaultResolver implements PermissionsResolver {

	@Override
	public boolean permission(Player player, String permCmd) {
		if (player.isOp())
			return true;
		if (permCmd.equals("/achievements") || permCmd.equals("/ach"))
			return true;
		if (permCmd.equals("/listach") || permCmd.equals("/listachievements"))
			return true;
		if (permCmd.equals("/stats"))
			return true;
		return false;
	}

	@Override
	public String getGroup(String player) {
		return "";
	}

	@Override
	public boolean inGroup(Player player, String group) {
		return false;
	}

	@Override
	public String getGroupPrefix(String player) {
		return "";
	}

	@Override
	public String getGroupSuffix(String player) {
		return "";
	}

	@Override
	public boolean canGroupBuild(String group) {
		return false;
	}

	@Override
	public boolean check() {
		return true;
	}

	@Override
	public void reloadPerms() {
	}

}
