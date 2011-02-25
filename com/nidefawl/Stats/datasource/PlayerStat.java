package com.nidefawl.Stats.datasource;

import java.io.File;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.block.Block;

public abstract class PlayerStat {
	private String name;
	public HashMap<String, Category> categories;
	static final Logger log = Logger.getLogger("Minecraft");
	private double moveDistance = 0;
	private double minecartDistance = 0;
	private double boatDistance = 0;
	private int lastBoatEnter = 0;
	private int lastMinecartEnter = 0;
	public int skipTeleports = 0;
	Block lastFace = null;
	public boolean unload = false;

	PlayerStat(String name) {
		this.name = name;
		this.categories = new HashMap<String, Category>();
		int now = (int) (System.currentTimeMillis() / 1000L);
		lastBoatEnter = lastMinecartEnter = now;
	}

	public Category get(String name) {
		return categories.get(name);
	}

	public Set<String> getCats() {
		return categories.keySet();
	}

	public Category newCategory(String name) {
		Category category = new Category();
		categories.put(name, category);
		return category;
	}

	public void put(String category, String key, int val) {
		Category cat;
		if (!categories.containsKey(category))
			cat = newCategory(category);
		else
			cat = categories.get(category);

		cat.put(key, val);
	}

	protected void copy(PlayerStat from)
	{
		this.name = from.name;
		this.categories = new HashMap<String, Category>(from.categories);
	}

	public void convertFlatFile(String directory) {
		PlayerStat psold = new PlayerStatFile(name, directory);
		psold.load();
		copy(psold);

		String location = directory + "/" + name + ".txt";
		File fold = new File(location);
		File fnew = new File(location + ".bak");
		fold.renameTo(fnew);
	}

	public abstract void save();

	public abstract void load();

	public void UpdateMove(double distance) {
		moveDistance += distance;
		if(moveDistance>10.0F) {
			Category cat = categories.get("stats");
			if (cat == null)
				cat = newCategory("stats");
			cat.add("move", 10);
			moveDistance = 0;
		}
	}
	public void UpdateMinecartMove(double distance) {
		minecartDistance += distance;
		if(minecartDistance>=10.0F) {
			Category cat = categories.get("minecart");
			if (cat == null)
				cat = newCategory("minecart");
			cat.add("move", 10);
			minecartDistance = 0;
		}
	}

	public void UpdateBoatMove(double distance) {
		boatDistance += distance;
		if(boatDistance>=10.0F) {
			Category cat = categories.get("boat");
			if (cat == null)
				cat = newCategory("boat");
			cat.add("move", 10);
			boatDistance = 0;
		}
	}

	public void setLastMinecartEnter(int lastMinecartEnter) {
		this.lastMinecartEnter = lastMinecartEnter;
	}

	public int getLastMinecartEnter() {
		return lastMinecartEnter;
	}

	public void setLastBoatEnter(int lastBoatEnter) {
		this.lastBoatEnter = lastBoatEnter;
	}

	public int getLastBoatEnter() {
		return lastBoatEnter;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}