package com.nidefawl.Stats.Permissions;

import org.bukkit.entity.Player;

public interface PermissionsResolver {

	public abstract boolean check();

	public abstract boolean permission(Player player, String permCmd);

	public abstract String getGroup(String player);

	public abstract boolean inGroup(Player player, String group);

	public abstract String getGroupPrefix(String player);

	public abstract String getGroupSuffix(String player);

	public abstract boolean canGroupBuild(String group);

	public abstract void reloadPerms();

}