package com.nidefawl.Stats.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.nidefawl.Stats.Stats;
import com.nidefawl.Stats.StatsSettings;

public class StatsSQLConnectionManager {
	static Connection connMySQL = null;
	static Connection connSQLite = null;

	public final static String getPreparedPlayerStatUpdateStatement() {
		return "UPDATE " + StatsSettings.dbTable + " set value=? where player = ? and category = ? and stat = ?;";
	}

	public final static String getPreparedPlayerStatInsertStatement() {
		return "INSERT INTO " + StatsSettings.dbTable + " (player,category,stat,value) VALUES(?,?,?,?);";
	}

	public static Connection getConnection(boolean MySQL) {
		try {
			if (MySQL) {
				if (connMySQL == null || connMySQL.isClosed()) {
					Class.forName("com.mysql.jdbc.Driver");
					connMySQL = DriverManager.getConnection(StatsSettings.dbUrl, StatsSettings.dbUsername, StatsSettings.dbPassword);
				}
				return connMySQL;
			} else {
				if (connSQLite == null || connSQLite.isClosed()) {
					Class.forName("org.sqlite.JDBC");
					connSQLite = DriverManager.getConnection(StatsSettings.liteDb);
				}
				return connSQLite;
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
	}

	public static void closeConnection(boolean MySQL) {
		try {
			if (MySQL) {
				if (connMySQL != null && !connMySQL.isClosed()) {
					connMySQL.close();
				}
			} else {
				if (connSQLite != null && !connSQLite.isClosed()) {
					connSQLite.close();
				}
			}
		} catch (SQLException e) {
			Stats.LogError("Error closing SQL-connection: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
