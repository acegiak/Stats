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

	@Override
	public void save(boolean close) {
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = StatsSQLConnectionManager.getConnection();
			if (close) {
				conn.setAutoCommit(false);
			}
			for (String catName : categories.keySet()) {
				Category cat = categories.get(catName);
				if (!cat.modified) {
					continue;
				}
				for (String statName : cat.stats.keySet()) {
					int value = cat.get(statName);
					ps = conn.prepareStatement(StatsSQLConnectionManager.getPreparedPlayerStatUpdateStatement());

					ps.setInt(1, value);
					ps.setString(2, getName());
					ps.setString(3, catName);
					ps.setString(4, statName);
					if (ps.executeUpdate() == 0) {
						Stats.LogInfo("new stat!!!!");
						ps = conn.prepareStatement(StatsSQLConnectionManager.getPreparedPlayerStatInsertStatement());
						ps.setString(1, getName());
						ps.setString(2, catName);
						ps.setString(3, statName);
						ps.setInt(4, value);
						ps.executeUpdate();
					}
				}
				cat.modified = false;
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
				if (conn != null && close) {
					conn.close();
				}
			} catch (SQLException ex) {
				Stats.LogError("SQL exception: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
	}

	@Override
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
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException ex) {
				Stats.LogError("SQL exception (on close): " + ex.getMessage());
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void save() {
		save(true);
	}
}