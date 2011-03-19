package com.nidefawl.Stats.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.nidefawl.Stats.Stats;
import com.nidefawl.Stats.StatsSettings;

public class StatsSQLConnectionManager {
	static Connection conn = null;

	public final static String getPreparedPlayerStatUpdateStatement() {
		return "UPDATE " + StatsSettings.dbTable + " set value=? where player = ? and category = ? and stat = ?;";
	}

	public final static String getPreparedPlayerStatInsertStatement() {
		return "INSERT INTO " + StatsSettings.dbTable + " (player,category,stat,value) VALUES(?,?,?,?);";
	}

	public static Connection getConnection() {
		try {
			if (conn == null || conn.isClosed()) {
				if (StatsSettings.useMySQL) {
					Class.forName("com.mysql.jdbc.Driver");
					conn = DriverManager.getConnection(StatsSettings.dbUrl, StatsSettings.dbUsername, StatsSettings.dbPassword);
				} else {
					Class.forName("org.sqlite.JDBC");
					conn = DriverManager.getConnection(StatsSettings.liteDb);
				}
			}
		} catch (SQLException e) {
			Stats.LogError("Error getting SQL-connection: " + e.getMessage());
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			Stats.LogError("Error getting SQL-connection: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
		return conn;
	}

	public static void closeConnection() {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			Stats.LogError("Error closing SQL-connection: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
