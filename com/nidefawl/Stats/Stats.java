package com.nidefawl.Stats;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.nidefawl.Achievements.Achievements;
import com.nidefawl.Stats.ItemResolver.hModItemResolver;
import com.nidefawl.Stats.ItemResolver.itemResolver;
import com.nidefawl.Stats.Permissions.GroupManagerResolver;
import com.nidefawl.Stats.Permissions.NijiPermissionsResolver;
import com.nidefawl.Stats.Permissions.PermissionsResolver;
import com.nidefawl.Stats.Permissions.defaultResolver;
import com.nidefawl.Stats.datasource.Category;
import com.nidefawl.Stats.datasource.PlayerStat;
import com.nidefawl.Stats.datasource.PlayerStatSQL;
import com.nidefawl.Stats.datasource.StatsSQLConnectionManager;
import com.nidefawl.Stats.udpates.Update1;
import com.nidefawl.Stats.udpates.Update2;
import com.nidefawl.Stats.util.Updater;

public class Stats extends JavaPlugin {
	public final static Logger log = Logger.getLogger("Minecraft");
	public final static double version = 0.9D;
	public final static String logprefix = "[Stats-" + version + "]";
	public final static String defaultCategory = "stats";
	public boolean enabled = false;
	public boolean updated = false;
	protected HashMap<String, PlayerStat> stats = new HashMap<String, PlayerStat>();
	protected HashMap<String, Long> lastPlayerActivity = new HashMap<String, Long>();
	protected itemResolver items = null;
	private static PermissionsResolver perms = null;
	private static StatsPlayerListener playerListener;
	private static StatsVehicleListener vehicleListener;
	private static StatsBlockListener blockListener;
	private static StatsEntityListener entityListener;
	private static StatsServerListener serverListener;
	long lastDebugWrite = System.currentTimeMillis();
	/**
	 * LWC updater
	 * 
	 * TODO: Remove when Bukkit has an updater that is working
	 */
	private Updater updater;

	/**
	 * @return the Updater instance
	 */
	public Updater getUpdater() {
		return updater;
	}
	public PlayerStat getPlayerStat(String name) {
		return stats.get(name);
	}
	public PermissionsResolver Perms() {
		if (perms == null) {
			LogInfo("Recreating PermissionsResolver");
			CreatePermissionResolver();
			if (perms == null)
				LogError("Couldn't link PermissionsResolver!");
		}
		return perms;
	}

	public void ReloadPerms() {
		if (perms != null) {
			perms.reloadPerms();
		}
	}

	public static void LogError(String Message) {
		log.log(Level.SEVERE, logprefix + " " + Message);
	}

	public static void LogInfo(String Message) {
		log.info(logprefix + " " + Message);
	}

	private boolean checkSchema() {
		Connection conn = null;
		DatabaseMetaData dbm = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean result = false;
		try {
			conn = StatsSQLConnectionManager.getConnection(StatsSettings.useMySQL);
			dbm = conn.getMetaData();
			rs = dbm.getTables(null, null, StatsSettings.dbTable, null);
			if (!rs.next()) {
				ps = conn.prepareStatement("CREATE TABLE `" + StatsSettings.dbTable + "` (" + "`player` varchar(32) NOT NULL DEFAULT '-'," + "`category` varchar(32) NOT NULL DEFAULT 'stats'," + "`stat` varchar(32) NOT NULL DEFAULT '-'," + "`value` int(11) NOT NULL DEFAULT '0',"
						+ "PRIMARY KEY (`player`,`category`,`stat`));");
				ps.executeUpdate();
				LogInfo("created table '" + StatsSettings.dbTable + "'");
			}
			result = true;
		} catch (SQLException ex) {
			LogError("SQL exception" + ex);
			ex.printStackTrace();
			result = false;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				LogError("SQL exception (on close)" + ex);
				ex.printStackTrace();
				result = false;
			}
		}
		return result;
	}

	//check if new items already added
	//if not then write them to file and update stat-keys in database
	private void update2() {
		
	}
	public void setSavedStats(CommandSender sender, String player, String category, String key, String value) {
		ArrayList<String> tounload = new ArrayList<String>();
		tounload.addAll(stats.keySet());
		for (String name : tounload) {
			unload(name);
		}

		stats.clear();
		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = StatsSQLConnectionManager.getConnection(StatsSettings.useMySQL);
			StringBuilder statement = new StringBuilder();
			int conditions = 0;
			statement.append("UPDATE " + StatsSettings.dbTable + " set value = ?");
			if (!player.equals("*"))
				statement.append((conditions++ == 0 ? " where" : " and") + " player = ?");
			if (!category.equals("*"))
				statement.append((conditions++ == 0 ? " where" : " and") + " category = ?");
			if (!key.equals("*"))
				statement.append((conditions++ == 0 ? " where" : " and") + " stat = ?");

			ps = conn.prepareStatement(statement.toString());
			ps.setString(1, value);
			conditions++;
			if (!key.equals("*"))
				ps.setString(conditions--, key);
			if (!category.equals("*"))
				ps.setString(conditions--, category);
			if (!player.equals("*"))
				ps.setString(conditions--, player);
			result = ps.executeUpdate();
		} catch (SQLException ex) {
			LogError("SQL exception" + ex);
			ex.printStackTrace();
			sender.sendMessage(StatsSettings.premessage + ex.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				LogError("SQL exception (on close)" + ex);
				ex.printStackTrace();
				sender.sendMessage(StatsSettings.premessage + ex.getMessage());
			}
		}
		sender.sendMessage(StatsSettings.premessage + "Updated " + result + " stats.");
		for (Player p : getServer().getOnlinePlayers()) {
			load(p);
		}
	}

	public int editPlayerStat(PlayerStat ps, String category, String key, String value) {

		int statsEdited = 0;
		if (category.equals("*")) {
			for (String catName : ps.categories.keySet()) {
				if (key.equals("*")) {
					for (String keyName : ps.categories.get(catName).getEntries()) {
						ps.categories.get(catName).set(keyName, Integer.valueOf(value));
						statsEdited++;
					}
				} else {
					if (!ps.categories.get(catName).getEntries().contains(key))
						continue;
					ps.categories.get(catName).set(key, Integer.valueOf(value));
					statsEdited++;
				}

			}
		} else {
			if (ps.categories.containsKey(category)) {
				if (key.equals("*")) {
					for (String keyName : ps.categories.get(category).getEntries()) {
						ps.categories.get(category).set(keyName, Integer.valueOf(value));
						statsEdited++;
					}
				} else {
					if (!ps.categories.get(category).getEntries().contains(key))
						return statsEdited;
					ps.categories.get(category).set(key, Integer.valueOf(value));
					statsEdited++;
				}
			}
		}
		return statsEdited;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (commandLabel.equals("played") && Perms().permission(player, "stats.view.playtime")) {
				int playedFor = get(player.getName(), "stats", "playedfor");
				if (playedFor == 0) {
					Messaging.send(player, StatsSettings.premessage + "No Playedtime yet!");
					return true;
				}
				Messaging.send(player, StatsSettings.premessage + "You played for "+ChatColor.WHITE + GetTimeString(playedFor));
				return true;
			} 
		}
		if (commandLabel.equals("stats")) {
			if (args.length == 1 && args[0].equalsIgnoreCase("help") || ((sender instanceof ConsoleCommandSender) && args.length == 0)) {
				if ((sender instanceof Player) && Perms().permission(sender, "stats.view.playtime")) {
					sender.sendMessage(StatsSettings.premessage + ChatColor.WHITE + "/played - Shows your play-time");
				}
				if ((sender instanceof Player) && Perms().permission(sender, "stats.view.own")) {
					sender.sendMessage(StatsSettings.premessage + ChatColor.WHITE + "/stats - Shows your stats summary");
				}
				if(Perms().permission(sender, "stats.view.others"))  {
					sender.sendMessage(StatsSettings.premessage + ChatColor.WHITE + "/stats <player> - Shows players stats summary");
				}
				if (Perms().permission(sender, "stats.admin")) {
					sender.sendMessage(StatsSettings.premessage + ChatColor.WHITE + "/stats list - Shows loaded players");
					sender.sendMessage(StatsSettings.premessage + ChatColor.WHITE + "/stats set <player> <cat> <stat> <val> - Set stats manually");
					sender.sendMessage(StatsSettings.premessage + ChatColor.WHITE + "/stats debug - Prints stat-update messages to console.");
					sender.sendMessage(StatsSettings.premessage + "Usage: " + ChatColor.WHITE + "/stats [category|debug|statname|list|helpset]");
					sender.sendMessage(StatsSettings.premessage + "or /stats [player] [category|statname]");
				} else {
					sender.sendMessage(StatsSettings.premessage + "Usage: " + ChatColor.WHITE + "/stats [category|statname|help] or /stats [player] [category|statname]");
				}
				return true;
			}
			else if (args.length == 1 && args[0].equalsIgnoreCase("list") && Perms().permission(sender, "stats.admin")) {
				sender.sendMessage(StatsSettings.premessage + ChatColor.WHITE + "Loaded playerstats (" + stats.size() + "): " + StatsPlayerList());
				return true;
			}
			else if (args.length > 0 && args[0].equalsIgnoreCase("set") && Perms().permission(sender, "stats.admin")) {
				if (args.length < 5) {
					sender.sendMessage(StatsSettings.premessage + ChatColor.RED + "Need more arguments (use * to select all)");
					sender.sendMessage(StatsSettings.premessage + ChatColor.WHITE + "/stats set [player] [category] [key] [value]- Set stats manually");
					return true;
				}
				try {
					Integer.valueOf(args[4]);
				} catch (Exception e) {
					sender.sendMessage(StatsSettings.premessage + ChatColor.WHITE + "[value] should be a number (" + args[4] + " is not)!");
					return true;
				}
				setSavedStats(sender, args[1], args[2], args[3], args[4]);
				return true;
			}
			else if (args.length == 1 && args[0].equalsIgnoreCase("debug") && Perms().permission(sender, "stats.admin")) {
				StatsSettings.debugOutput = !StatsSettings.debugOutput;
				sender.sendMessage(StatsSettings.premessage + ChatColor.WHITE + "Debugging " + (StatsSettings.debugOutput ? "enabled. Check server log." : "disabled."));
				return true;
			}
			if(!Perms().permission(sender, "stats.view.own")) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to view your stats!");
				return true;
			}
			Player who = null;
			if(sender instanceof Player) {
				who = (Player)sender;
			}
			int offs = 0;
			if(args.length>0) {
				who = playerMatch(args[0]);
				if(who!=null) {
					if(!Perms().permission(sender, "stats.view.others")) {
						sender.sendMessage(ChatColor.RED + "You don't have permission to view others stats!");
						return true;
					}
					offs++;
				} else {
					if ((sender instanceof ConsoleCommandSender)) {
						sender.sendMessage(ChatColor.RED + "Player '"+args[0]+"' is not online!");
						return false;
					} else {
						who = (Player)sender;
					}
				}
			}
			if (args.length == offs + 1) {
				if (isStat(who.getName(), args[offs])) {
					printStat(sender, who, "stats", args[offs]);
					return true;
				} else if (getItems().getItem(args[offs]) != 0 && !(args[offs].equals("boat") || args[offs].equals("minecart"))) {
					printStat(sender, who, "blockcreate", args[offs]);
					printStat(sender, who, "blockdestroy", args[offs]);
					return true;
				} else if (isCat(who.getName(), args[offs])) {
					sender.sendMessage(StatsSettings.premessage + "Please choose: (/stats " + args[offs] + " <stat-name>)");
					sender.sendMessage(StatsSettings.premessage + ChatColor.WHITE + getCatEntries(who.getName(), args[offs]));
					return true;
				} else {
					sender.sendMessage(StatsSettings.premessage + ChatColor.RED + "stat/category '" + args[offs] + "' not found. Possible values:");
					sender.sendMessage(StatsSettings.premessage + ChatColor.WHITE + getCats(who.getName()));
					return true;
				}
			} else if (args.length == offs + 2) {
				if (isCat(who.getName(), args[offs])) {
					printStat(sender, who, args[offs], args[offs + 1]);
					return true;
				} else {
					sender.sendMessage(StatsSettings.premessage + ChatColor.RED + "stat/category '" + args[offs] + "' not found. Possible values:");
					sender.sendMessage(StatsSettings.premessage + ChatColor.WHITE + getCats(who.getName()));
					return true;
				}
			} 
			
			int playedTime = get(who.getName(), "stats", "playedfor");
			int movedBlocks = get(who.getName(), "stats", "move");
			int totalCreate = get(who.getName(), "stats", "totalblockcreate");
			int totalDestroy = get(who.getName(), "stats", "totalblockdestroy");
			int tkills = get(who.getName(), "kills", "total");
			int tdeaths = get(who.getName(), "deaths", "total");
			int pdeaths = get(who.getName(), "deaths", "player");
			int pkills = get(who.getName(), "kills", "player");
			int totalDamage = get(who.getName(), "damagetaken", "total");
			int totalDamageDealt = get(who.getName(), "damagedealt", "total");
			try {
				sender.sendMessage("------------------------------------------------");
				sender.sendMessage(ChatColor.GOLD + " stats for " + ChatColor.WHITE + who.getName() + ChatColor.GOLD + ": (" + ChatColor.WHITE + "/stats help for more" + ChatColor.GOLD + ")");
				sender.sendMessage("------------------------------------------------");
				String s1 = ChatColor.GOLD + "[" + ChatColor.YELLOW + "Playedtime" + ChatColor.GOLD + "]" + ChatColor.YELLOW;
				while (MinecraftFontWidthCalculator.getStringWidth(sender,s1) < 120)
					s1 += " ";
				s1 += ChatColor.WHITE + GetTimeString(playedTime);
				sender.sendMessage(s1);
				s1 = ChatColor.GOLD + "[" + ChatColor.YELLOW + "Moved" + ChatColor.GOLD + "]" + ChatColor.YELLOW;
				while (MinecraftFontWidthCalculator.getStringWidth(sender,s1) < 120)
					s1 += " ";
				s1 += ChatColor.WHITE + String.valueOf(movedBlocks) + " blocks";
				sender.sendMessage(s1);
				printStatFormatted(sender, "Blocks", "created", totalCreate, "destroyed", totalDestroy);
				printStatFormatted(sender, "Deaths", "total", tdeaths, "player", pdeaths);
				printStatFormatted(sender, "Kills", "total", tkills, "player", pkills);
				printStatFormatted(sender, "Damage", "dealt", totalDamageDealt, "taken", totalDamage);
				sender.sendMessage("------------------------------------------------");
			} catch (Exception e) {
				// TODO: handle exception
			}
			return true;
		}

		return false;
	}

	private void printStatFormatted(CommandSender sender, String name, String title1, int value1, String title2, int value2) {
		String s1 = ChatColor.GOLD + "[" + ChatColor.YELLOW + name + ChatColor.GOLD + "]" + ChatColor.YELLOW;
		while (MinecraftFontWidthCalculator.getStringWidth(sender,s1) < 120)
			s1 += " ";
		if (title2 != null)
			s1 += ChatColor.WHITE + title1 + "/" + title2;
		else
			s1 += ChatColor.WHITE + title1;
		while (MinecraftFontWidthCalculator.getStringWidth(sender,s1) < 240)
			s1 += " ";
		if (title2 != null)
			s1 += value1 + "/" + value2;
		else
			s1 += value1;
		sender.sendMessage(s1);
	}

	public void printStat(CommandSender sendTo, Player statPlayer, String cat, String stat) {
		long statVal = get(statPlayer.getName(), cat, stat);
		String statString = "" + statVal;
		if (stat.equalsIgnoreCase("playedfor")) {
			statString = GetTimeString((int) statVal);
		}
		if (stat.equalsIgnoreCase("lastlogout") || stat.equalsIgnoreCase("lastlogin")) {
			Date logDate = new Date(statVal * 1000);
			SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yy hh:mm");
			statString = format.format(logDate);
		}

		sendTo.sendMessage(StatsSettings.premessage + cat + "/" + stat + ": " + ChatColor.WHITE + statString);
	}

	public String GetTimeString(int Seconds) {
		int days = (int) Math.ceil(Seconds / (24 * 3600));
		int hours = (int) Math.ceil((Seconds - (24 * 3600 * days)) / 3600);
		int minutes = (int) Math.ceil((Seconds - (24 * 3600 * days + 3600 * hours)) / 60);
		String timeString = "";
		timeString += days + "d " + hours + "h " + minutes + "m";
		return timeString;
	}

	public void CreatePermissionResolver() {

		Plugin permPlugin = this.getServer().getPluginManager().getPlugin("GroupManager");
		if (permPlugin != null) {
			log.info(logprefix + " Using GroupManager for permissions");
			perms = new GroupManagerResolver(this);
			return;
		}
		permPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
		if (permPlugin != null) {
			log.info(logprefix + " Using Nijis Permissions for permissions");
			perms = new NijiPermissionsResolver(this);
			return;
		}

		log.info(logprefix + " Using bukkit's isOp() for permissions (until other plugin is enabled)");
		perms = new defaultResolver();
		return;
	}

	public String StatsPlayerList() {
		if (stats.size() == 0)
			return "No players loaded";
		int length = (stats.size() - 1);

		int on = 0;
		String list = "";
		for (String currentName : stats.keySet()) {
			if (currentName == null) {
				++on;
				continue;
			}

			list += (on >= length) ? currentName : currentName + ", ";
			++on;
		}
		list += " ";
		return list;
	}
	protected final FilenameFilter filter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			if(name.equals("items.txt")) return false;
			return name.endsWith(".txt");
		}
	};

	protected final FilenameFilter filterOld = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return name.endsWith(".txt.old");
		}
	};
	public void convertFlatFiles() {
		String[] files = getDataFolder().list(filterOld);
		if (files != null && files.length > 0) {
			for (int i = 0; i < files.length; i++) {
				String basename = files[i].substring(0, files[i].lastIndexOf("."));
				File fnew = new File(getDataFolder(), files[i]);
				File fold = new File(getDataFolder(), basename);
				fnew.renameTo(fold);
			}
		}
		files = getDataFolder().list(filter);
		if (files == null || files.length == 0) {
		}

		int count = 0;
		PlayerStatSQL ps;
		for (int i = 0; i < files.length; i++) {
			File fold = new File(getDataFolder(), files[i]);
			if (!fold.exists())
				continue;

			String basename = files[i].substring(0, files[i].lastIndexOf("."));
			ps = new PlayerStatSQL(basename, this);
			ps.convertFlatFile(getDataFolder().getPath());
			ps.save();
			count++;
		}
		if(count > 0) {
			Stats.LogInfo("Converted " + count + " stat files to " + (StatsSettings.useMySQL ? "MySQL" : "SQLite"));
		}
	}

	public Stats() {

	}

	public void onEnable() {
		getDataFolder().mkdirs();
		File statsDirectory = new File("stats");
		if (statsDirectory.exists() && statsDirectory.isDirectory()) {
			File intSettings = new File("stats", "internal.ini");
			if (intSettings.exists()) {
				intSettings.delete();
			}
			LogInfo("Moving ./stats/ directory to " + getDataFolder().getPath());
			if (!statsDirectory.renameTo(new File(getDataFolder().getPath()))) {
				LogError("Moving ./stats/ directory to " + getDataFolder().getPath() + " failed");
				LogError("Please move your files manually and delete the old 'stats' directory. Thanks");
				LogError("Disabling Stats");
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
		}
		StatsSettings.load(this);
		updater = new Updater(this);
		try {
			updated = updater.updateDist(StatsSettings.autoUpdate);
			if (updated) {
				LogInfo("UPDATE INSTALLED. PLEASE RESTART....");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Connection conn = StatsSQLConnectionManager.getConnection(StatsSettings.useMySQL);
		try {
			if (conn == null || conn.isClosed()) {
				LogError("Could not establish SQL connection. Disabling Stats");
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
		} catch (SQLException e) {
			LogError("Could not establish SQL connection. Disabling Stats");
			e.printStackTrace();
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		if (!checkSchema()) {
			LogError("Could not create table. Disabling Stats");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		convertFlatFiles();
		if (updated)
			return;
		Update1.execute(this);
		Update2.execute(this);
		items = new hModItemResolver(new File(getDataFolder(),"items.txt"));
		update2();
		stats = new HashMap<String, PlayerStat>();
		CreatePermissionResolver();
		enabled = true;
		playerListener = new StatsPlayerListener(this);
		blockListener = new StatsBlockListener(this);
		entityListener = new StatsEntityListener(this);
		vehicleListener = new StatsVehicleListener(this);
		serverListener = new StatsServerListener(this);
		initialize();
		LogInfo("Plugin Enabled");
		for (Player p : getServer().getOnlinePlayers()) {
			load(p);
		}
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new SaveTask(this), StatsSettings.delay * 20, StatsSettings.delay * 20);
	}

	public Player playerMatch(String name) {
		List<Player> list = getServer().matchPlayer(name);
		for (Player p : list)
			if (p != null && p.getName().equalsIgnoreCase(name))
				return p;
		return null;
	}

	public static class SaveTask implements Runnable {
		private Stats statsInstance;

		public SaveTask(Stats plugin) {
			statsInstance = plugin;
		}

		@Override
		public void run() {
			if (!statsInstance.enabled)
				return;
			statsInstance.saveAll();
		}
	}

	public void onDisable() {
		if (enabled) {
			saveAll();
			Plugin achPlugin = getServer().getPluginManager().getPlugin("Achievements");
			if (achPlugin != null && achPlugin.isEnabled()) {
				if (((Achievements) achPlugin).enabled) {
					((Achievements) achPlugin).checkAchievements();
					((Achievements) achPlugin).Disable();
				}
			}
			enabled = false;
			getServer().getScheduler().cancelTasks(this);
			stats = null;
			updater.saveInternal();
			StatsSQLConnectionManager.closeConnection(StatsSettings.useMySQL);
		}
		LogInfo("Plugin Disabled");
	}

	public void initialize() {
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Highest, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ANIMATION, playerListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_DROP_ITEM, playerListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_PICKUP_ITEM, playerListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.BLOCK_IGNITE, blockListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Lowest, this);
		getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Highest, this);
		getServer().getPluginManager().registerEvent(Event.Type.VEHICLE_ENTER, vehicleListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.VEHICLE_MOVE, vehicleListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_DISABLE, serverListener, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_ENABLE, serverListener, Priority.Normal, this);
	}

	public void updateStat(Player player, String statType, boolean resetAfkTimer) {
		updateStat(player, statType, 1, resetAfkTimer);
	}

	public void updateStat(Player player, String statType, int num, boolean resetAfkTimer) {
		updateStat(player.getName(), defaultCategory, statType, num, resetAfkTimer);
	}

	public void updateStat(Player player, String statType, Block block, boolean resetAfkTimer) {
		updateStat(player, statType, block, 1, resetAfkTimer);
	}

	public void updateStat(Player player, String statType, Block block, int num, boolean resetAfkTimer) {
		if (block.getTypeId() <= 0)
			return;
		String blockName = getItems().getItem(block.getTypeId());
		updateStat(player.getName(), statType, blockName, num, resetAfkTimer);
	}

	public void updateStat(Player player, String category, String key, int val, boolean resetAfkTimer) {
		updateStat(player.getName(), category, key, val, resetAfkTimer);
	}

	public void updateStat(String player, String category, String key, int val, boolean resetAfkTimer) {
		if (!enabled)
			return;
		if (player == null || player.length() < 1) {
			LogError("updateStat got empty player for [" + category + "] [" + key + "] [" + val + "]");
			return;
		}

		PlayerStat ps = stats.get(player);
		if (ps == null)
			return;
		Category cat = ps.get(category);
		if (cat == null)
			cat = ps.newCategory(category);
		cat.add(key, val);
		if (resetAfkTimer)
			ps.setLastUpdate();
		if (StatsSettings.debugOutput)
			log.info(logprefix + " [DEBUG]: adding " + val + " to " + category + "/" + key + " of " + player);
	}

	public void setStat(String player, String category, String key, int val) {
		PlayerStat ps = stats.get(player);
		if (ps == null)
			return;
		ps.put(category, key, val);
	}

	public boolean isCat(String player, String category) {
		PlayerStat ps = stats.get(player);
		if (ps == null)
			return false;
		Category cat = ps.get(category);
		if (cat == null)
			return false;
		return true;
	}

	String viewStats(String player, String stat) {
		return stat;

	}

	public void updateMove(String player, Location from, Location to) {
		if (!enabled)
			return;
		if (player == null || player.length() < 1) {
			LogError("updateMove got empty player for " + player);
			return;
		}
		PlayerStat ps = stats.get(player);
		if (ps == null)
			return;
		ps.UpdateMove(from.toVector().distance(to.toVector()));

	}

	public void updateVehicleMove(String player, Vehicle vhc, Location from, Location to) {
		if (!enabled)
			return;
		if (player == null || player.length() < 1) {
			LogError("updateVehicleMove got empty player for " + player);
			return;
		}
		PlayerStat ps = stats.get(player);
		if (ps == null)
			return;
		double Distance = from.toVector().distance(to.toVector());
		if (vhc instanceof org.bukkit.entity.Boat) {
			ps.UpdateBoatMove(Distance);
		} else if (vhc instanceof org.bukkit.entity.Minecart) {
			ps.UpdateMinecartMove(Distance);
		}
	}

	public String getCatEntries(String player, String category) {
		PlayerStat ps = stats.get(player);
		if (ps == null)
			return "player not found";
		Set<String> cats = ps.getCats();
		if (cats.size() == 0)
			return "no categories founnd";

		Category cat = ps.get(category);
		if (cat == null)
			return "category not found";
		Set<String> entris = cat.getEntries();
		int length = (entris.size() - 1);

		int on = 0;
		String list = "";
		for (String currentName : entris) {
			if (currentName == null) {
				++on;
				continue;
			}

			list += (on >= length) ? currentName : currentName + ", ";
			++on;
		}
		list += " ";
		return list;
	}

	public String getCats(String player) {
		PlayerStat ps = stats.get(player);
		if (ps == null)
			return "no categories found";
		Set<String> cats = ps.getCats();
		if (cats.size() == 0)
			return "no categories found";
		int length = (cats.size() - 1);

		int on = 0;
		String list = "";
		for (String currentName : cats) {
			if (currentName == null) {
				++on;
				continue;
			}

			list += (on >= length) ? currentName : currentName + ", ";
			++on;
		}
		list += " ";
		return list;
	}

	public boolean isStat(String player, String stat) {
		PlayerStat ps = stats.get(player);
		if (ps == null)
			return false;
		Category cat = ps.get("stats");
		if (cat == null)
			return false;
		if (cat.get(stat) == 0)
			return false;
		return true;
	}

	public int get(String player, String category, String key) {
		PlayerStat ps = stats.get(player);
		if (ps == null)
			return 0;
		Category cat = ps.get(category.toLowerCase());
		if (cat == null && ps.get(category) != null)
			cat = ps.get(category);
		if (cat == null)
			return 0;
		if (cat.get(key) == 0 && cat.get(key.toLowerCase()) != 0)
			return cat.get(key.toLowerCase());
		return cat.get(key);
	}

	public void load(Player player) {
		if (!Perms().permission(player, "stats.log")) {
			if (StatsSettings.debugOutput)
				LogInfo("player " + player.getName() + " has no stats.log permission. Not loading/logging actions");
			return;
		}
		if (stats.containsKey(player.getName())) {
			LogError("attempting to load already loaded player: " + player.getName());
			return;
		}
		PlayerStat ps = new PlayerStatSQL(player.getName(), this);
		ps.load();
		ps.skipTeleports = 2;
		stats.put(player.getName(), ps);
		if (StatsSettings.debugOutput)
			LogInfo("player " + player.getName() + " has been loaded.");
	}

	public void unload(String player) {
		entityListener.UnloadPlayer(player);
		if (stats.containsKey(player)) {
			PlayerStat ps = stats.get(player);
			ps.save();
			stats.remove(player);
			return;
		}
	}

	public boolean isAfk(Player p) {
		if (!stats.containsKey(p.getName()))
			return false;
		return stats.get(p.getName()).isAfk();
	}

	public void saveAll() {
		if (StatsSettings.debugOutput)
			log.info("Stats debug: saving " + stats.size() + " players stats");
		try {
			Connection conn = StatsSQLConnectionManager.getConnection(StatsSettings.useMySQL);
			if (conn == null)
				return;
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for (PlayerStat stat : stats.values()) {
			if (stat == null || playerMatch(stat.getName()) == null) {
				stat.unload = true;
				continue;
			}
			if (StatsSettings.afkTimer > 0 && !stat.isAfk()) {
				updateStat(stat.getName(), defaultCategory, "playedfor", (int) StatsSettings.delay, false);
			} else if (StatsSettings.debugOutput) {
				log.info("Stats debug: not updating playedfor for afk player " + stat.getName());
			}
			stat.save(false);
		}
		StatsSQLConnectionManager.closeConnection(StatsSettings.useMySQL);
		for (PlayerStat stat : stats.values()) {
			if (!stat.unload)
				continue;
			LogError("onPlayerQuit did not happen, unloading " + stat.getName() + " now");
			logout(stat.getName());
			unload(stat.getName());
		}
	}

	public void setItems(itemResolver items) {
		this.items = items;
	}

	public itemResolver getItems() {
		return items;
	}

	public void login(Player player) {
		int lastLog = get(player.getName(), defaultCategory, "lastlogin");
		int now = (int) (System.currentTimeMillis() / 1000L);
		if (now - lastLog > StatsSettings.loginRateLimit) {
			updateStat(player, "login", true);
		}
		setStat(player.getName(), defaultCategory, "lastlogin", now);
	}

	public void logout(String player) {
		int now = (int) (System.currentTimeMillis() / 1000L);
		setStat(player, defaultCategory, "lastlogout", now);
	}

	public void updateVehicleEnter(String player, Vehicle vhc) {
		if (!enabled)
			return;
		if (player == null || player.length() < 1) {
			LogError("updateVehicleEnter got empty player for " + player);
			return;
		}
		PlayerStat ps = stats.get(player);
		if (ps == null)
			return;
		int now = (int) (System.currentTimeMillis() / 1000L);

		if (vhc instanceof org.bukkit.entity.Boat) {
			if (now - ps.getLastBoatEnter() > 60) {
				updateStat(player, "boat", "enter", 1, true);
				ps.setLastBoatEnter(now);
			}

		} else if (vhc instanceof org.bukkit.entity.Minecart) {
			if (now - ps.getLastMinecartEnter() > 60) {
				updateStat(player, "minecart", "enter", 1, true);
				ps.setLastMinecartEnter(now);
			}
		}
	}

	public void onLoad() {

	}
	/**
	 * @param perms the perms to set
	 */
	public static void setPerms(PermissionsResolver perms) {
		Stats.perms = perms;
	}

}