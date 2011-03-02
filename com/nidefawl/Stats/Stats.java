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
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.nidefawl.Achievements.Achievements;
import com.nidefawl.Stats.ItemResolver.hModItemResolver;
import com.nidefawl.Stats.ItemResolver.itemResolver;
import com.nidefawl.Stats.ItemResolver.myGeneralItemResolver;
import com.nidefawl.Stats.Permissions.GroupUserResolver;
import com.nidefawl.Stats.Permissions.NijiPermissionsResolver;
import com.nidefawl.Stats.Permissions.PermissionsResolver;
import com.nidefawl.Stats.Permissions.defaultResolver;
import com.nidefawl.Stats.datasource.Category;
import com.nidefawl.Stats.datasource.PlayerStat;
import com.nidefawl.Stats.datasource.PlayerStatSQL;
import com.nidefawl.Stats.datasource.StatsSQLConnectionManager;
import com.nidefawl.Stats.util.Updater;

public class Stats extends JavaPlugin {
	public final static Logger log = Logger.getLogger("Minecraft");
	public final static double version = 0.7;
	public final static String logprefix = "[Stats-" + version + "]";
	public final static String defaultCategory = "stats";
	public boolean enabled = false;
	public boolean updated = false;
	protected HashMap<String, PlayerStat> stats = new HashMap<String, PlayerStat>();
	protected itemResolver items = new hModItemResolver("items.txt");
	private static PermissionsResolver perms = null;
	private static StatsPlayerListener playerListener;
	private static StatsVehicleListener vehicleListener;
	private static StatsBlockListener blockListener;
	private static StatsEntityListener entityListener;
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

	public PermissionsResolver Perms() {
		if (perms == null) {
			log.info(logprefix + " Recreating Nijis Permissions for permissions");
			CreatePermissionResolver();
			if (perms == null)
				log.log(Level.SEVERE, logprefix + " Couldn't link to Nijis Permissions plugin!!!");
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
			conn = StatsSQLConnectionManager.getConnection();
			dbm = conn.getMetaData();
			rs = dbm.getTables(null, null, StatsSettings.dbTable, null);
			if (!rs.next()) {
				ps = conn.prepareStatement("CREATE TABLE `" + StatsSettings.dbTable + "` (" + "`player` varchar(32) NOT NULL DEFAULT '-'," + "`category` varchar(32) NOT NULL DEFAULT 'stats'," + "`stat` varchar(32) NOT NULL DEFAULT '-'," + "`value` int(11) NOT NULL DEFAULT '0',"
						+ "PRIMARY KEY (`player`,`category`,`stat`));");
				ps.executeUpdate();
				log.info(logprefix + " " + this.getClass().getName() + " created table '" + StatsSettings.dbTable + "'.");
			}
			result = true;
		} catch (SQLException ex) {
			log.log(Level.SEVERE, logprefix + " " + this.getClass().getName() + " SQL exception", ex);
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
				log.log(Level.SEVERE, logprefix + " " + this.getClass().getName() + " SQL exception on close", ex);
				result = false;
			}
		}
		return result;
	}

	public void setSavedStats(Player admin, String player, String category, String key, String value) {
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
			conn = StatsSQLConnectionManager.getConnection();
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
			log.log(Level.SEVERE, logprefix + " " + this.getClass().getName() + " SQL exception", ex);
			Messaging.send(admin, StatsSettings.premessage + ex.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (SQLException ex) {
				log.log(Level.SEVERE, logprefix + " " + this.getClass().getName() + " SQL exception on close", ex);
				Messaging.send(admin, StatsSettings.premessage + ex.getMessage());
			}
		}
		Messaging.send(admin, StatsSettings.premessage + "Updated " + result + " stats.");
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
		if (!(sender instanceof Player))
			return false;
		Player player = (Player) sender;
		if (commandLabel.equals("played") && Perms().permission(player, "/stats")) {
			int playedFor = get(player.getName(), "stats", "playedfor");
			if (playedFor == 0) {
				Messaging.send(player, StatsSettings.premessage + "No Playedtime yet!");
				return true;
			}
			Messaging.send(player, StatsSettings.premessage + "You played for &f" + GetTimeString(playedFor));
			return true;
		}
		if (commandLabel.equals("stats") && Perms().permission(player, "/stats")) {
			if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
				Messaging.send(player, StatsSettings.premessage + ChatColor.WHITE + "/stats - Shows your stats summary");
				Messaging.send(player, StatsSettings.premessage + ChatColor.WHITE + "/stats <player> - Shows players stats summary");
				if (Perms().permission(player, "/statsadmin")) {
					Messaging.send(player, StatsSettings.premessage + ChatColor.WHITE + "/stats list - Shows loaded players");
					Messaging.send(player, StatsSettings.premessage + ChatColor.WHITE + "/stats set - Set stats manually");
					Messaging.send(player, StatsSettings.premessage + ChatColor.WHITE + "/stats debug - Prints stat-update messages to console.");
					Messaging.send(player, StatsSettings.premessage + "Usage: " + ChatColor.WHITE + "/stats [category|debug|statname|list|helpset]");
					Messaging.send(player, StatsSettings.premessage + "or /stats [player] [category|statname]");
				} else {
					Messaging.send(player, StatsSettings.premessage + "Usage: " + ChatColor.WHITE + "/stats [category|statname|help] or /stats [player] [category|statname]");
				}
				return true;
			}
			if (Perms().permission(player, "/statsadmin")) {
				if (args.length == 1 && args[0].equalsIgnoreCase("list") && Perms().permission(player, "/statsadmin")) {
					Messaging.send(player, StatsSettings.premessage + ChatColor.WHITE + "Loaded playerstats (" + stats.size() + "): " + StatsPlayerList());
					return true;
				}
				if (args.length == 1 && args[0].equalsIgnoreCase("entlist")) {
					entityListener.sendEntList(player);
					return true;
				}
				if (args.length > 0 && args[0].equalsIgnoreCase("set")) {
					if (args.length < 5) {
						Messaging.send(player, StatsSettings.premessage + ChatColor.RED + "Need more arguments (use * to select all)");
						Messaging.send(player, StatsSettings.premessage + ChatColor.WHITE + "/stats set [player] [category] [key] [value]- Set stats manually");
						return true;
					}
					try {
						Integer.valueOf(args[4]);
					} catch (Exception e) {
						Messaging.send(player, StatsSettings.premessage + ChatColor.WHITE + "[value] should be a number (" + args[4] + " is not)!");
						return true;
					}
					setSavedStats(player, args[1], args[2], args[3], args[4]);
					return true;
				}
				if (args.length == 1 && args[0].equalsIgnoreCase("debug")) {
					StatsSettings.debugOutput = !StatsSettings.debugOutput;
					Messaging.send(player, StatsSettings.premessage + ChatColor.WHITE + "Debugging " + (StatsSettings.debugOutput ? "enabled. Check server log." : "disabled."));
					return true;

				}
				if (args.length == 1 && args[0].equalsIgnoreCase("disable")) {
					enabled = false;
					Messaging.send(player, StatsSettings.premessage + ChatColor.WHITE + "Debugging: Stats is now disabled.");
					return true;

				}
				if (args.length == 1 && args[0].equalsIgnoreCase("enable")) {
					enabled = true;
					Messaging.send(player, StatsSettings.premessage + ChatColor.WHITE + "Debugging: Stats is now enabled.");
					return true;

				}
			}
			Player who = player;
			if (args.length > 0) {
				int offs = 1;
				who = playerMatch(args[0]);
				if (who == null) {
					who = player;
					offs--;
				}
				if (args.length == offs + 1) {
					if (isStat(player.getName(), args[offs])) {
						printStat(player, who, "stats", args[offs]);
						return true;
					} else if (getItems().getItem(args[offs]) != 0 && !(args[offs].equals("boat") || args[offs].equals("minecart"))) {
						printStat(player, who, "blockcreate", args[offs]);
						printStat(player, who, "blockdestroy", args[offs]);
						return true;
					} else if (isCat(player.getName(), args[offs])) {
						Messaging.send(player, StatsSettings.premessage + "Please choose: (/stats " + args[offs] + " <stat-name>)");
						Messaging.send(player, StatsSettings.premessage + ChatColor.WHITE + getCatEntries(who.getName(), args[offs]));
						return true;
					} else {
						Messaging.send(player, StatsSettings.premessage + ChatColor.RED + "stat/category '" + args[offs] + "' not found. Possible values:");
						Messaging.send(player, StatsSettings.premessage + ChatColor.WHITE + getCats(who.getName()));
						return true;
					}
				} else if (args.length == offs + 2) {
					if (isCat(player.getName(), args[offs])) {
						printStat(player, who, args[offs], args[offs + 1]);
						return true;
					} else {
						Messaging.send(player, StatsSettings.premessage + ChatColor.RED + "stat/category '" + args[offs] + "' not found. Possible values:");
						Messaging.send(player, StatsSettings.premessage + ChatColor.WHITE + getCats(who.getName()));
						return true;
					}
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
				Messaging.send(player, "------------------------------------------------");
				Messaging.send(player, "&e stats for &f" + who.getName() + "&e: (&f/stats help for more&e)");
				Messaging.send(player, "------------------------------------------------");
				String s1 = "&6 [&ePlayedtime&6]";
				while (MinecraftFontWidthCalculator.getStringWidth(s1) < 110)
					s1 += " ";
				s1 += "&f" + GetTimeString(playedTime);
				Messaging.send(player, s1);
				s1 = "&6 [&eMoved&6]";
				while (MinecraftFontWidthCalculator.getStringWidth(s1) < 110)
					s1 += " ";
				s1 += "&f" + movedBlocks + " blocks";
				Messaging.send(player, s1);
				printStatFormated(player, "Blocks", "created", totalCreate, "destroyed", totalDestroy);
				printStatFormated(player, "Deaths", "total", tdeaths, "player", pdeaths);
				printStatFormated(player, "Kills", "total", tkills, "player", pkills);
				printStatFormated(player, "Damage", "dealt", totalDamageDealt, "taken", totalDamage);
				Messaging.send(player, "------------------------------------------------");
			} catch (Exception e) {
				// TODO: handle exception
			}
			return true;

		}

		return false;
	}

	private void printStatFormated(Player p, String name, String title1, int value1, String title2, int value2) {
		String s1 = "&6 [&e" + name + "&6]&e";
		while (MinecraftFontWidthCalculator.getStringWidth(s1) < 120)
			s1 += " ";
		if (title2 != null)
			s1 += "&f" + title1 + "/" + title2;
		else
			s1 += "&f" + title1;
		while (MinecraftFontWidthCalculator.getStringWidth(s1) < 240)
			s1 += " ";
		if (title2 != null)
			s1 += value1 + "/" + value2;
		else
			s1 += value1;
		Messaging.send(p, s1);
	}

	public void printStat(Player sendTo, Player statPlayer, String cat, String stat) {
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

		Messaging.send(sendTo, StatsSettings.premessage + cat + "/" + stat + ": &f" + statString);
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
		Plugin permPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
		if (permPlugin != null) {
			log.info(logprefix + " Using Nijis Permissions for permissions");
			perms = new NijiPermissionsResolver(this);
			return;
		}
		permPlugin = this.getServer().getPluginManager().getPlugin("GroupUsers");
		if (permPlugin != null) {
			log.info(logprefix + " Using GroupUsers for permissions");
			perms = new GroupUserResolver(this);
			return;
		}
		log.info(logprefix + " Using bukkit's isOp() for permissions");
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

	public void convertFlatFiles() {
		File dir = new File(StatsSettings.directory);
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".txt");
			}
		};

		FilenameFilter filterOld = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".txt.old");
			}
		};
		String[] files = dir.list(filterOld);
		if (files != null && files.length > 0) {
			for (int i = 0; i < files.length; i++) {
				String location = StatsSettings.directory + File.separator + files[i];
				String basename = files[i].substring(0, files[i].lastIndexOf("."));
				File fnew = new File(location);
				File fold = new File(StatsSettings.directory + File.separator + basename);
				fnew.renameTo(fold);
			}
		}
		files = dir.list(filter);
		if (files == null || files.length == 0) {
		}

		int count = 0;
		PlayerStatSQL ps;
		for (int i = 0; i < files.length; i++) {
			String location = StatsSettings.directory + File.separator + files[i];
			File fold = new File(location);
			if (!fold.exists())
				continue;

			String basename = files[i].substring(0, files[i].lastIndexOf("."));
			ps = new PlayerStatSQL(basename, this);
			ps.convertFlatFile(StatsSettings.directory);
			ps.save();
			count++;
		}
		Stats.LogInfo("Converted " + count + " stat files to " + (StatsSettings.useMySQL ? "MySQL" : "SQLite"));
	}

	public Stats() {
		StatsSettings.initialize();
		updater = new Updater();
		System.setProperty("org.sqlite.lib.path", updater.getOSSpecificFolder());
		StatsSQLConnectionManager.getConnection();
		try {
			if (StatsSettings.autoUpdate) {
				
				updated = updater.checkDist();
				updated |= updater.checkAchDist();
				if(updated) {
					LogInfo("UPDATE INSTALLED. PLEASE RESTART....");
					return;
				}
				
			} else {
				updater.check();
			}
			updater.update();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (StatsSettings.useMySQL) {
				Class.forName("com.mysql.jdbc.Driver");
			} else {
				Class.forName("org.sqlite.JDBC");
			}
		} catch (ClassNotFoundException e) {
			LogError("JDBC driver for " + (StatsSettings.useMySQL ? "MySQL" : "SQLite") + " not found. Disabling Stats");
			getServer().getPluginManager().disablePlugin(this);
			e.printStackTrace();
			return;
		}
		Connection conn = StatsSQLConnectionManager.getConnection();
		if (conn == null) {
			LogError("Could not establish SQL connection. Disabling Stats");
			getServer().getPluginManager().disablePlugin(this);
			return;
		} else {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return;
			}
		}
		if (!checkSchema()) {
			LogError("Could not create table. Disabling Stats");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		convertFlatFiles();
	}

	public void onEnable() {
		if(updated) return;
		if (new File("plugins/MyGeneral.jar").exists()) {
			Plugin myPlug = this.getServer().getPluginManager().getPlugin("MyGeneral");
			if (myPlug != null) {
				LogInfo("Using MyGeneral Item Resolver");
				setItems(new myGeneralItemResolver(myPlug));
			}
		}
		stats = new HashMap<String, PlayerStat>();
		CreatePermissionResolver();
		enabled = true;
		playerListener = new StatsPlayerListener(this);
		blockListener = new StatsBlockListener(this);
		entityListener = new StatsEntityListener(this);
		vehicleListener = new StatsVehicleListener(this);
		initialize();
		LogInfo("Plugin Enabled");
		for (Player p : getServer().getOnlinePlayers()) {
			load(p);
		}

		getServer().getScheduler().scheduleAsyncRepeatingTask(this, new SaveTask(this), StatsSettings.delay * 20, StatsSettings.delay * 20);
		
		Plugin ach = this.getServer().getPluginManager().getPlugin("Achievements");
		if(ach!=null) {
			if(((Achievements)ach).enabled) {
				((Achievements)ach).Disable();
			} else if(!ach.isEnabled()) {
				ach.onEnable();
			}
			
			((Achievements)ach).Enable();
		}
	}

	public Player playerMatch(String name) {
		List<Player> list = getServer().matchPlayer(name);
		for (Player p : list)
			if (p != null && p.getName().equalsIgnoreCase(name))
				return p;
		return null;
	}

	private static class SaveTask implements Runnable {
		private Stats statsInstance;

		SaveTask(Stats plugin) {
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
		saveAll();
		Plugin ach = this.getServer().getPluginManager().getPlugin("Achievements");
		if(ach!=null) {
			if(((Achievements)ach).enabled) {
				((Achievements)ach).Disable();
			}
		}
		enabled = false;
		getServer().getScheduler().cancelTasks(this);
		stats = null;
		updater.saveInternal();
		log.info(logprefix + " " + version + " Plugin Disabled");
	}

	public void initialize() {
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ANIMATION, playerListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ITEM, playerListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_DROP_ITEM, playerListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_PICKUP_ITEM, playerListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.BLOCK_PLACED, blockListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.BLOCK_RIGHTCLICKED, blockListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.BLOCK_INTERACT, blockListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.BLOCK_IGNITE, blockListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGED, entityListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.VEHICLE_ENTER, vehicleListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.VEHICLE_MOVE, vehicleListener, Priority.Monitor, this);
	}

	public void updateStat(Player player, String statType) {
		updateStat(player, statType, 1);
	}

	public void updateStat(Player player, String statType, int num) {
		updateStat(player.getName(), defaultCategory, statType, num);
	}

	public void updateStat(Player player, String statType, Block block) {
		updateStat(player, statType, block, 1);
	}

	public void updateStat(Player player, String statType, Block block, int num) {
		if (block.getTypeId() <= 0)
			return;
		String blockName = getItems().getItem(block.getTypeId());
		updateStat(player.getName(), statType, blockName, num);
	}

	public void updateStat(Player player, String category, String key, int val) {
		updateStat(player.getName(), category, key, val);
	}

	public void updateStat(String player, String category, String key, int val) {
		if (!enabled)
			return;
		if (player == null || player.length() < 1) {
			log.log(Level.SEVERE, logprefix + " updateStat got empty player for [" + category + "] [" + key + "] [" + val + "]");
			return;
		}

		PlayerStat ps = stats.get(player);
		if (ps == null)
			return;
		Category cat = ps.get(category);
		if (cat == null)
			cat = ps.newCategory(category);
		cat.add(key, val);
		if (StatsSettings.debugOutput)
			log.info(logprefix + " [DEBUG]: adding " + val + " to " + category + "/" + key + " of " + player);
	}

	public void updateStatUnsafe(String player, String category, String key, int val) {
		if (!enabled)
			return;
		if (player == null || player.length() < 1) {
			log.log(Level.SEVERE, logprefix + " updateStat got empty player for [" + category + "] [" + key + "] [" + val + "]");
			return;
		}

		PlayerStat ps = stats.get(player);
		if (ps == null)
			return;
		Category cat = ps.get(category);
		if (cat == null)
			cat = ps.newCategory(category);
		cat.addUnsafe(key, val);
		if (StatsSettings.debugOutput)
			log.info(logprefix + " [DEBUG]: adding " + val + " to " + category + "/" + key + " of " + player + " without touching modifed flag");
	}

	protected void setStat(String player, String category, String key, int val) {
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

	protected void updateMove(String player, Location from, Location to) {
		if (!enabled)
			return;
		if (player == null || player.length() < 1) {
			log.log(Level.SEVERE, logprefix + " updateMove got empty player for " + player);
			return;
		}
		PlayerStat ps = stats.get(player);
		if (ps == null)
			return;
		ps.UpdateMove(from.toVector().distance(to.toVector()));

	}

	protected void updateVehicleMove(String player, Vehicle vhc, Location from, Location to) {
		if (!enabled)
			return;
		if (player == null || player.length() < 1) {
			log.log(Level.SEVERE, logprefix + " updateVehicleMove got empty player for " + player);
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

	protected void load(Player player) {
		if (!Perms().permission(player, "/stats")) {
			if (StatsSettings.debugOutput)
				log.info(logprefix + " player " + player.getName() + " has no /stats permission. Not loading/logging actions");
			return;
		}
		if (stats.containsKey(player.getName())) {
			log.log(Level.SEVERE, logprefix + " attempting to load already loaded player: " + player.getName());
			return;
		}
		PlayerStat ps = new PlayerStatSQL(player.getName(), this);
		ps.load();
		ps.skipTeleports = 2;
		stats.put(player.getName(), ps);
		if (StatsSettings.debugOutput)
			log.info(logprefix + " player " + player.getName() + " has been loaded.");
	}

	protected void unload(String player) {
		entityListener.UnloadPlayer(player);
		if (stats.containsKey(player)) {
			PlayerStat ps = stats.get(player);
			ps.save();
			stats.remove(player);
			return;
		}
	}

	private void saveAll() {
		if (StatsSettings.debugOutput)
			log.info("Stats debug: saving " + stats.size() + " players stats");
		for (PlayerStat stat : stats.values()) {
			if (stat == null || playerMatch(stat.getName()) == null) {
				stat.unload = true;
				continue;
			}
			updateStat(stat.getName(), defaultCategory, "playedfor", (int) StatsSettings.delay);
			stat.save();
		}
		for (PlayerStat stat : stats.values()) {
			if (!stat.unload)
				continue;
			log.log(Level.SEVERE, logprefix + " " + " onDisconnect did not happen, logging out+ unloading " + stat.getName() + " now");
			logout(stat.getName());
			unload(stat.getName());
		}
	}

	public void setItems(itemResolver items) {
		this.items = items;
	}

	public itemResolver getItems() {
		if (items == null) {
			Plugin myPlug = this.getServer().getPluginManager().getPlugin("MyGeneral");
			if (myPlug != null) {
				setItems(new myGeneralItemResolver(myPlug));
			} else
				setItems(new hModItemResolver("items.txt"));
		}
		return items;
	}

	public void login(Player player) {
		int lastLog = get(player.getName(), defaultCategory, "lastlogin");
		int now = (int) (System.currentTimeMillis() / 1000L);
		if (now - lastLog > StatsSettings.loginRateLimit) {
			updateStat(player, "login");
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
			log.log(Level.SEVERE, logprefix + " updateVehicleEnter got empty player for " + player);
			return;
		}
		PlayerStat ps = stats.get(player);
		if (ps == null)
			return;
		int now = (int) (System.currentTimeMillis() / 1000L);

		if (vhc instanceof org.bukkit.entity.Boat) {
			if (now - ps.getLastBoatEnter() > 60) {
				updateStat(player, "boat", "enter", 1);
				ps.setLastBoatEnter(now);
			}

		} else if (vhc instanceof org.bukkit.entity.Minecart) {
			if (now - ps.getLastMinecartEnter() > 60) {
				updateStat(player, "minecart", "enter", 1);
				ps.setLastMinecartEnter(now);
			}
		}
	}

}