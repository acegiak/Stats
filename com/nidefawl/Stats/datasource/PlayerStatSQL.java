package com.nidefawl.Stats.datasource;

import java.util.logging.Logger;
import java.sql.*;

import com.nidefawl.Stats.Stats;
import com.nidefawl.Stats.StatsSettings;


public class PlayerStatSQL extends PlayerStat {
	static final Logger log = Logger.getLogger("Minecraft");
	public final String logprefix = "[Stats-" + Stats.version + "]";
	Stats plugin = null;

	public PlayerStatSQL(String name, Stats plugin) {
		super(name);
		this.plugin = plugin;
	}

	public void save() {
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = StatsSQLConnectionManager.getConnection();
			conn.setAutoCommit(false);
			for(String catName : categories.keySet()) {
				Category cat = categories.get(catName);
				if (!cat.modified) {
					continue;
				}
				for(String statName : cat.stats.keySet()) {
					int value = cat.get(statName);
					ps = conn.prepareStatement("UPDATE " + StatsSettings.dbTable + " set value=? where player = ? and category = ? and stat = ?;", Statement.RETURN_GENERATED_KEYS);
					
					ps.setInt(1, value);
					ps.setString(2, getName());
					ps.setString(3, catName);
					ps.setString(4, statName);
					if(ps.executeUpdate()==0) {
						ps = conn.prepareStatement("INSERT INTO " + StatsSettings.dbTable + " (player,category,stat,value) VALUES(?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
						ps.setString(1, getName());
						ps.setString(2, catName);
						ps.setString(3, statName);						
						ps.setInt(4, value);
						ps.executeUpdate();
					}
				}
				cat.modified=false;
			}
			conn.commit();
		} catch (SQLException ex) {
			Stats.LogError("SQL exception: " + ex.getMessage());
			ex.printStackTrace();
		} finally {
			try {
                if (ps != null) {
                    ps.close();
                }
            	if(conn != null) {
                    conn.close();
            	}
			} catch (SQLException ex) {
				Stats.LogError("SQL exception: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
	}

	public void load() {
		if (!plugin.enabled)
			return;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = StatsSQLConnectionManager.getConnection();
			ps = conn.prepareStatement("SELECT * from " + StatsSettings.dbTable + " where player = ?");
			ps.setString(1, getName());
			rs = ps.executeQuery();
			while (rs.next()) {
				put(rs.getString("category"), rs.getString("stat"), rs.getInt("value"));
			}
		} catch (SQLException ex) {
			Stats.LogError("SQL exception: " + ex.getMessage());
			ex.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
            	if(conn != null) {
                    conn.close();
            	}
			} catch (SQLException ex) {
				Stats.LogError("SQL exception (on close): " + ex.getMessage());
				ex.printStackTrace();
			}
		}
	}
}