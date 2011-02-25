package com.nidefawl.Stats.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.nidefawl.Stats.Stats;
import com.nidefawl.Stats.StatsSettings;


public class StatsSQLConnectionManager {
    public static Connection getConnection() {
        try {
	        if (StatsSettings.useMySQL) {
	    		Class.forName("com.mysql.jdbc.Driver");
	    		Connection ret = DriverManager.getConnection(StatsSettings.dbUrl, StatsSettings.dbUsername, StatsSettings.dbPassword);
	            ret.setAutoCommit(true);
	            return ret;
	        } else {
				Class.forName("org.sqlite.JDBC");
	        	Connection ret = DriverManager.getConnection(StatsSettings.liteDb);
	            ret.setAutoCommit(true);
	            return ret;
	        }      	
        } catch (SQLException e) {
            Stats.LogError("Error getting SQL-connection: "+e.getMessage());
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            Stats.LogError("Error getting SQL-connection: "+e.getMessage());
			e.printStackTrace();
            return null;
		}
    }

 
}
