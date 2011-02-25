package com.nidefawl.Stats;

import java.io.File;

import org.bukkit.ChatColor;



public class StatsSettings {

	public static String directory = "stats";
	public static String liteDb;
	public static int loginRateLimit = 3600;
	public static long delay = 30;
	public static String dbUrl;
	public static String dbUsername;
	public static String dbPassword;
	public static String dbTable;
	public static boolean deathNotifying;
	public static boolean debugOutput;
	public static boolean autoUpdate;
	public static boolean useMySQL;
	public static String libPath;
	public static String premessage = ChatColor.YELLOW + "[Stats]" + ChatColor.WHITE;

	public static void initialize() {
		if (!new File(directory).exists()) {
			try {
				(new File(directory)).mkdir();
			} catch (Exception ex) {
				Stats.LogError("Unable to create " + directory + " directory");
			}
			Stats.LogInfo("directory '" + directory + "' created!");
			Stats.LogInfo("make sure to check stats/stats.properties and mysql.properties ");
		}
	    liteDb = "jdbc:sqlite:" + directory + File.separator + "stats.db";
		loadPropertiesFiles();
	}
	
	public static void onDisable() {
	}

	private static void loadPropertiesFiles() {
		
		PropertiesFile properties = new PropertiesFile(new File(directory + File.separator + "stats.properties"));
		delay = (long) properties.getInt("stats-save-delay", 30, "delay between automatic saving (seconds)");
		loginRateLimit = properties.getInt("stats-login-delay", 3600, "limit between login-count increases");
		
		boolean useSQL = properties.getBoolean("stats-use-sql");
		properties.remove("stats-use-sql");
		String dataSource = properties.getString("stats-datasource", useSQL?"mysql":"sqlite", "dropped flatfile support");
		if(dataSource.toLowerCase().equals("mysql")) {
			useMySQL  = true; 
		} else {
			useMySQL  = false;
		}
		
		premessage = properties.getString("stats-message-prefix", "&e[Stats]&f", "");
		debugOutput = properties.getBoolean("stats-debug", false, "");
		deathNotifying = properties.getBoolean("stats-deathnotify", true, "");
		autoUpdate = properties.getBoolean("stats-autoUpdate", true, "");
		properties.save();
		if (premessage.length() > 0)
			if (premessage.charAt(premessage.length() - 1) != ' ')
				premessage += " ";
		properties = new PropertiesFile(new File("mysql.properties"));
		dbUrl = properties.getString("sql-db", "jdbc:mysql://localhost:3306/minecraft", "");
		dbUsername = properties.getString("sql-user", "root", "");
		dbPassword = properties.getString("sql-pass", "root", "");
		dbTable = properties.getString("sql-table-stats", "stats", "");
		properties.save();
	}

}
