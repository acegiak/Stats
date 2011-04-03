/**
 * This file is taken from LWC (https://github.com/Hidendra/LWC)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.nidefawl.Stats.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import com.nidefawl.Achievements.Achievements;
import com.nidefawl.Stats.Stats;

public class Updater {

	/**
	 * URL to the base update site
	 */
	private final static String UPDATE_SITE = "http://dev.craftland.org/stats/";

	/**
	 * File used to obtain the latest version
	 */
	private final static String VERSION_FILE = "VERSIONS";

	/**
	 * Internal config
	 */
	private HashMap<String, String> config = new HashMap<String, String>();

	private Stats plugin = null;

	public Updater(Stats plugin) {
		this.plugin = plugin;
		config.put("sqlite", "1.00");
		config.put("mysql", "1.00");
		parseInternalConfig();
	}
	/**
	 * @return the current sqlite version
	 */
	public double getCurrentSQLiteVersion() {
		return Double.parseDouble(config.get("sqlite"));
	}
	/**
	 * @return the current sqlite version
	 */
	public double getCurrentMySQLVersion() {
		return Double.parseDouble(config.get("mysql"));
	}
	public String combineSplit(int startIndex, String[] string, String seperator) {
		if (string.length == 0)
			return "";
		StringBuilder builder = new StringBuilder();
		for (int i = startIndex; i < string.length; i++) {
			builder.append(string[i]);
			builder.append(seperator);
		}
		if (builder.length() > seperator.length())
			builder.deleteCharAt(builder.length() - seperator.length()); // remove
		return builder.toString();
	}

	/**
	 * @return the latest sqlite version
	 */
	public double getLatestSQLiteVersion() {
		try {
			URL url = new URL(UPDATE_SITE + VERSION_FILE);

			InputStream inputStream = url.openStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

			bufferedReader.readLine();
			double version = Double.parseDouble(bufferedReader.readLine());

			bufferedReader.close();

			return version;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0.00;
	}

	/**
	 * @return the internal config file
	 */
	private File getInternalFile() {
		return new File(plugin.getDataFolder() + File.separator + "internal.ini");
	}

	/**
	 * Parse the internal config file
	 */
	private void parseInternalConfig() {
		try {
			File file = getInternalFile();

			if (!file.exists()) {
				saveInternal();
				return;
			}

			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;

			while ((line = reader.readLine()) != null) {
				if (line.trim().startsWith("#")) {
					continue;
				}

				if (!line.contains(":")) {
					continue;
				}

				/*
				 * Split the array
				 */
				String[] arr = line.split(":");

				if (arr.length < 2) {
					continue;
				}

				/*
				 * Get the key/value
				 */
				String key = arr[0];
				String value = combineSplit(1, arr, ":");
				// value = value.substring(0, value.length() - 1);

				/*
				 * Set the config value
				 */
				config.put(key, value);
			}

			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the internal updater config file
	 */
	public void saveInternal() {
		try {
			File file = getInternalFile();

			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));

			writer.write("# Stats Internal Config\n");
			writer.write("#################################\n");
			writer.write("### DO NOT MODIFY THIS FILE   ###\n");
			writer.write("### THIS DOES NOT CHANGE      ###\n");
			writer.write("### STATS'S VISIBLE BEHAVIOUR ###\n");
			writer.write("#################################\n\n");
			writer.write("#################################\n");
			writer.write("###        THANK YOU!         ###\n");
			writer.write("#################################\n\n");

			for (String key : config.keySet()) {
				String value = config.get(key);

				writer.write(key + ":" + value + "\n");
			}

			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public boolean updateDist(boolean autoUpdate) throws Exception {
		URL url = new URL(UPDATE_SITE + VERSION_FILE);
		InputStream inputStream = url.openStream();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		double SQLiteVersion = Double.parseDouble(bufferedReader.readLine());
		double MySQLVersion = Double.parseDouble(bufferedReader.readLine());
		double StatsVersion = Double.parseDouble(bufferedReader.readLine());
		double AchievementsVersion = Double.parseDouble(bufferedReader.readLine());
		bufferedReader.close();
		inputStream.close();
		String plugPath = plugin.getDataFolder().getPath()+File.separator;
		boolean updated = false;
		updated |= (new UpdaterFile(UPDATE_SITE + "lib/mysql.jar",plugPath+"lib/mysql.jar",getCurrentMySQLVersion(),MySQLVersion)).update(true);
		config.put("mysql", String.valueOf(MySQLVersion));
		updated |= (new UpdaterFile(UPDATE_SITE + "lib/sqlite.jar",plugPath+"lib/sqlite.jar",getCurrentSQLiteVersion(),SQLiteVersion)).update(true);
		config.put("sqlite", String.valueOf(SQLiteVersion));
		if (new File("plugins/Achievements.jar").exists()) {
			updated |= (new UpdaterFile(UPDATE_SITE + "Achievements.jar","plugins/Achievements.jar",Achievements.getVersion(),AchievementsVersion)).update(autoUpdate);
		}
		updated |= (new UpdaterFile(UPDATE_SITE + "Stats.jar","plugins/Stats.jar",Stats.version,StatsVersion)).update(autoUpdate);
		saveInternal();
		return updated;
	}

}