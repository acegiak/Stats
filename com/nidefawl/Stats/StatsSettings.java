package com.nidefawl.Stats;

import java.io.File;

import org.bukkit.ChatColor;

public class StatsSettings {

	public static String liteDb;
	public static int loginRateLimit = 3600;
	public static long delay = 30;
	public static long afkTimer = 300;
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

	public static void load(Stats plugin) {

		PropertiesFile properties = new PropertiesFile(new File(plugin.getDataFolder(), "stats.properties"));
		liteDb = "jdbc:sqlite:" + plugin.getDataFolder().getPath() + File.separator + "stats.db";
		delay = properties.getInt("stats-save-delay", 30, "delay between automatic saving (seconds)");
		loginRateLimit = properties.getInt("stats-login-delay", 3600, "limit between login-count increases");
		afkTimer = properties.getInt("stats-afk-delay", 300, " (seconds) If there is no player-activity in this time playedfor does not get updated. Set to 0 to disable.");

		boolean useSQL = properties.getBoolean("stats-use-sql");
		properties.remove("stats-use-sql");
		String dataSource = properties.getString("stats-datasource", useSQL ? "mysql" : "sqlite", "dropped flatfile support");
		if (dataSource.toLowerCase().equals("mysql")) {
			useMySQL = true;
		} else {
			useMySQL = false;
		}

		premessage = properties.getString("stats-message-prefix", "&e[Stats]&f", "");
		debugOutput = properties.getBoolean("stats-debug", false, "");
		deathNotifying = properties.getBoolean("stats-deathnotify", true, "");
		autoUpdate = properties.getBoolean("stats-autoUpdate", true, "");
		if (premessage.length() > 0)
			if (premessage.charAt(premessage.length() - 1) != ' ')
				premessage += " ";
		dbUrl = properties.getString("sql-db", "jdbc:mysql://localhost:3306/minecraft", "");
		dbUsername = properties.getString("sql-user", "root", "");
		dbPassword = properties.getString("sql-pass", "root", "");
		dbTable = properties.getString("sql-table-stats", "stats", "");
		properties.save();
	}

}
