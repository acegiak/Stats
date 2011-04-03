package com.nidefawl.Stats.Permissions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class defaultResolver implements PermissionsResolver {

	@Override
	public boolean permission(CommandSender sender, String permCmd) {
		if (sender.isOp())
			return true;
		if(!(sender instanceof Player)) return false;
		if (permCmd.startsWith("achievements.view") || permCmd.equals("achievements.check"))
			return true;
		if (permCmd.startsWith("stats.view")||permCmd.equals("stats.log"))
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
	public boolean load() {
		return true;
	}

	@Override
	public void reloadPerms() {
	}

}
