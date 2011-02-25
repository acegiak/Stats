package com.nidefawl.Stats.ItemResolver;

import org.bukkit.plugin.Plugin;

import com.nidefawl.MyGeneral.MyGeneral;

public class myGeneralItemResolver implements itemResolver {
	public MyGeneral plugin;

	public myGeneralItemResolver(Plugin plugin) {
		this.plugin = (MyGeneral) plugin;
	}

	@Override
	public int getItem(String name) {
		return plugin.getDataSource().getItem(name);
	}

	@Override
	public String getItem(int id) {
		return plugin.getDataSource().getItem(id);
	}

}
