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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
	private final static String VERSION_FILE = "VERSIONDEV";

	/**
	 * File used for the distribution
	 */
	private final static String DIST_FILE = "Stats.jar";
	/**
	 * File used for the distribution
	 */
	private final static String ACHDIST_FILE = "Achievements.jar";

	/**
	 * List of files to download
	 */
	private List<UpdaterFile> needsUpdating = new ArrayList<UpdaterFile>();

	/**
	 * Internal config
	 */
	private HashMap<String, String> config = new HashMap<String, String>();

	public Updater() {
		//enableSSL();

		/*
		 * Default config values
		 */
		config.put("sqlite", "1.00");

		/*
		 * Parse the internal config
		 */
		parseInternalConfig();
	}

	/**
	 * Check for dependencies
	 * 
	 * @return true if Stats should be reloaded
	 */
	public void check() {
		String[] paths = new String[] { "lib/sqlite.jar", getFullNativeLibraryPath(), "lib/mysql.jar"  };

		paths = new String[] { "lib/sqlite.jar", getFullNativeLibraryPath(), "lib/mysql.jar"  };
		for (String path : paths) {
			File file = new File(path);

			if (file != null && !file.exists() && !file.isDirectory()) {
				UpdaterFile updaterFile = new UpdaterFile(UPDATE_SITE + path);
				updaterFile.setLocalLocation(path);

				needsUpdating.add(updaterFile);
			}
		}

		double latestVersion = getLatestPluginVersion();

		if (latestVersion > Stats.version) {
			Stats.LogInfo("Update detected for Stats");
			Stats.LogInfo("Latest version: " + latestVersion);
		}
		if (new File("plugins/Achievements.jar").exists()) {
			try {
				latestVersion = getLatestAchievemntsPluginVersion();
				if (latestVersion > Double.parseDouble(Achievements.version)) {
					Stats.LogInfo("Update detected for Achievements");
					Stats.LogInfo("Latest version: " + latestVersion);
				}
			}
			catch (Exception e) {
				Stats.LogError("Exception while updating Achievements plugin: "+e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Force update of binaries
	 */
	private void requireBinaryUpdate() {
		String[] paths = new String[] { "lib/sqlite.jar", getFullNativeLibraryPath() , "lib/mysql.jar"  };

		for (String path : paths) {
			UpdaterFile updaterFile = new UpdaterFile(UPDATE_SITE + path);
			updaterFile.setLocalLocation(path);

			needsUpdating.add(updaterFile);
		}
	}

	/**
	 * Check to see if the distribution is outdated
	 * 
	 * @return
	 */
	public boolean checkDist() {

		double latestVersion = getLatestPluginVersion();

		if (latestVersion > Stats.version) {
			UpdaterFile updaterFile = new UpdaterFile(UPDATE_SITE + DIST_FILE);
			updaterFile.setLocalLocation("plugins/Stats.jar");

			needsUpdating.add(updaterFile);

			try {
				update();
				Stats.LogInfo("Updated successful");
				return true;
			} catch (Exception e) {
				Stats.LogInfo("Update failed: " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			Stats.LogInfo("Stats plugin is up to date");
		}

		return false;
	}
	public boolean checkAchDist() {

		if (new File("plugins/Achievements.jar").exists()) {
			try {
				double latestVersion = getLatestAchievemntsPluginVersion();
				if (latestVersion > Double.parseDouble(Achievements.version)) {
					UpdaterFile updaterFile = new UpdaterFile(UPDATE_SITE + ACHDIST_FILE);
					updaterFile.setLocalLocation("plugins/Achievments.jar");
					needsUpdating.add(updaterFile);
					try {
						update();
						Stats.LogInfo("Updated successful");
						return true;
					} catch (Exception e) {
						Stats.LogInfo("Update failed: " + e.getMessage());
						e.printStackTrace();
					}
				} else {
					Stats.LogInfo("Achievements plugin is up to date ("+(Achievements.version)+")");
				}
			}
			catch (Exception e) {
				Stats.LogError("Exception while updating Achievements plugin: "+e);
				e.printStackTrace();
			}
		}
		return false;
	}
	/**
	 * Get the latest Achievemnts version
	 * 
	 * @return
	 */
	public double getLatestAchievemntsPluginVersion() {
		try {
			URL url = new URL(UPDATE_SITE + VERSION_FILE);

			InputStream inputStream = url.openStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

			bufferedReader.readLine();
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
	 * Get the latest version
	 * 
	 * @return
	 */
	public double getLatestPluginVersion() {
		try {
			URL url = new URL(UPDATE_SITE + VERSION_FILE);

			InputStream inputStream = url.openStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

			double version = Double.parseDouble(bufferedReader.readLine());

			bufferedReader.close();

			return version;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0.00;
	}

	/**
	 * @return the current sqlite version
	 */
	public double getCurrentSQLiteVersion() {
		return Double.parseDouble(config.get("sqlite"));
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
		return new File("stats" + File.separator + "internal.ini");
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
				String value = combineSplit(1,arr, ":");
				//value = value.substring(0, value.length() - 1);

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

	/**
	 * @return the full path to the native library for sqlite
	 */
	public String getFullNativeLibraryPath() {
		return getOSSpecificFolder() + getOSSpecificFileName();
	}

	/**
	 * @return the os/arch specific file name for sqlite's native library
	 */
	public String getOSSpecificFileName() {
		String osname = System.getProperty("os.name").toLowerCase();

		if (osname.contains("windows")) {
			return "sqlitejdbc.dll";
		} else if (osname.contains("mac")) {
			return "libsqlitejdbc.jnilib";
		} else { /* We assume linux/unix */
			return "libsqlitejdbc.so";
		}
	}

	/**
	 * @return the os/arch specific folder location for SQLite's native library
	 */
	public String getOSSpecificFolder() {
		String osname = System.getProperty("os.name").toLowerCase();
		String arch = System.getProperty("os.arch").toLowerCase();

		if (osname.contains("windows")) {
			return "lib/native/Windows/" + arch + "/";
		} else if (osname.contains("mac")) {
			return "lib/native/Mac/" + arch + "/";
		} else { /* We assume linux/unix */
			return "lib/native/Linux/" + arch + "/";
		}
	}

	/**
	 * Ensure we have all of the required files (if not, download them)
	 */
	public void update() throws Exception {
		/*
		 * Check internal versions
		 */
		double latestVersion = getLatestSQLiteVersion();
		if (latestVersion > getCurrentSQLiteVersion()) {
			requireBinaryUpdate();
			Stats.LogInfo("Binary update required");
			config.put("sqlite", latestVersion + "");
		}

		if (needsUpdating.size() == 0) {
			return;
		}

		/*
		 * Make the native folder hierarchy if needed
		 */
		File folder = new File(getOSSpecificFolder());
		folder.mkdirs();

		Stats.LogInfo("Need to download " + needsUpdating.size() + " file(s)");

		Iterator<UpdaterFile> iterator = needsUpdating.iterator();

		while (iterator.hasNext()) {
			UpdaterFile item = iterator.next();

			Stats.LogInfo(" - Downloading file : " + item.getRemoteLocation());

			URL url = new URL(item.getRemoteLocation());
			File file = new File(item.getLocalLocation());

			if (file.exists()) {
				file.delete();
			}

			InputStream inputStream = url.openStream();
			OutputStream outputStream = new FileOutputStream(file);

			saveTo(inputStream, outputStream);

			inputStream.close();
			outputStream.close();

			Stats.LogInfo("  + Download complete");
			iterator.remove();
		}

		/*
		 * In the event we updated binaries, we should force an ini save!
		 */
		saveInternal();
	}

	
	/**
	 * Write an input stream to an output stream
	 * 
	 * @param inputStream
	 * @param outputStream
	 */
	private void saveTo(InputStream inputStream, OutputStream outputStream) throws IOException {
		byte[] buffer = new byte[1024];
		int len = 0;

		while ((len = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, len);
		}
	}

}