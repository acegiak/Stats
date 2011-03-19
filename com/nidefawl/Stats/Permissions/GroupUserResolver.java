package com.nidefawl.Stats.Permissions;

import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.bukkit.authorblues.GroupUsers.GroupUsers;

public class GroupUserResolver implements PermissionsResolver {
	public static final Logger log = Logger.getLogger("Minecraft");
	JavaPlugin plugin = null;
	private GroupUsers perms = null;

	public GroupUserResolver(JavaPlugin plugin) {
		this.plugin = plugin;
		check();
	}

	@Override
	public boolean check() {
		if (perms == null) {
			Plugin checkPlugin = plugin.getServer().getPluginManager().getPlugin("GroupUsers");
			if (checkPlugin != null) {
				perms = (GroupUsers) checkPlugin;
			} else
				return false;
		}
		if (perms.isEnabled() == false)
			return false;
		return true;
	}

	@Override
	public boolean permission(Player player, String permCmd) {
		if (!check())
			return false;
		return perms.playerCanUseCommand(player, permCmd);
	}

	@Override
	public String getGroup(String player) {
		return "";
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
		return true;
	}

	@Override
	public boolean inGroup(Player player, String group) {
		if (!check())
			return false;
		return perms.isInGroup(player, group);
	}

	@Override
	public void reloadPerms() {
	}

}
