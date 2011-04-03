package com.nidefawl.Stats.datasource;

import java.io.File;
import java.util.Scanner;

import com.nidefawl.Stats.Stats;

public class PlayerStatFile extends PlayerStat {
	private String directory;

	PlayerStatFile(String name, String directory) {
		super(name);
		this.directory = directory;
	}

	@Override
	public void save() {
	}

	@Override
	public void load() {
		String location = directory + File.separator + getName() + ".txt";

		if (!new File(location).exists()) {
			return;
		}

		try {
			Scanner scanner = new Scanner(new File(location));
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.startsWith("#") || line.equals(""))
					continue;
				String[] split = line.split(":");
				if (split.length != 3) {
					Stats.LogInfo("Malformed line (" + line + ") in " + location);
					continue;
				}
				String category = split[0];
				String key = split[1];
				Integer val = Integer.parseInt(split[2]);

				put(category, key, val);
			}
		} catch (Exception ex) {
			Stats.LogInfo("Exception while reading " + location + " " + ex);
			return;
		}
	}

	@Override
	public void save(boolean close) {
	}
}