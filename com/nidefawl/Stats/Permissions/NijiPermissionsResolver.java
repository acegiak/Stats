package com.nidefawl.Stats.Permissions;

import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import com.nijikokun.bukkit.Permissions.Permissions;

public class NijiPermissionsResolver implements PermissionsResolver {
	public static final Logger log = Logger.getLogger("Minecraft");
	JavaPlugin plugin = null;
	private Permissions perms = null;

	public NijiPermissionsResolver(JavaPlugin plugin) {
		this.plugin = plugin;
		check();
	}

	public boolean check() {
		if (perms == null) {
			Plugin checkPlugin = plugin.getServer().getPluginManager().getPlugin("Permissions");
			if (checkPlugin != null) {
				if (!((Permissions) checkPlugin).isEnabled())
					return false;
				perms = (Permissions) checkPlugin;
			} else {
				return false;
			}
		}
		return true;
	}

	public boolean permission(Player player, String permCmd) {

		if (!check())
			return false;
		return perms.getHandler().permission(player, permCmd);
	}

	public String getGroup(String player) {
		if (!check())
			return "";
		return perms.getHandler().getGroup(player);
	}

	public String getGroupPrefix(String player) {
		if (!check())
			return "";
		return perms.getHandler().getGroupPrefix(player);
	}

	public String getGroupSuffix(String player) {
		if (!check())
			return "";
		return perms.getHandler().getGroupSuffix(player);
	}

	public boolean canGroupBuild(String group) {
		if (!check())
			return false;
		return perms.getHandler().canGroupBuild(group);
	}

	@Override
	public boolean inGroup(Player player, String group) {
		if (!check())
			return false;
		return perms.getHandler().inGroup(player.getName(), group);
	}

	@Override
	public void reloadPerms() {
		if (!check())
			return;
		perms.setupPermissions();
	}

}
