package com.nidefawl.Stats.datasource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

public class Category {
	public boolean modified = false;
	public HashMap<String, Integer> stats;
	static final Logger log = Logger.getLogger("Minecraft");

	public Category() {
		stats = new HashMap<String, Integer>();
		modified = false;
	}

	public int get(String name) {
		Integer data = stats.get(name);
		if (data == null)
			return 0;
		return data;
	}

	public Set<String> getEntries() {
		return stats.keySet();
	}

	public void put(String name, Integer value) {
		stats.put(name, value);
		modified = true;
	}

	public void set(String name, Integer value) {
		put(name, value);
	}

	public void add(String name, Integer value) {
		if (!stats.containsKey(name)) {
			put(name, value);
			return;
		}
		Integer oldval = stats.get(name);
		put(name, value + oldval);
	}

	Iterator<String> iterator() {
		return stats.keySet().iterator();
	}
}