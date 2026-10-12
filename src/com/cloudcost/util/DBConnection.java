package com.cloudcost.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    
    // Automatically creates 'cloudcost_db.mv.db' in your project root
    private static final String URL = "jdbc:h2:./cloudcost_db;AUTO_SERVER=TRUE";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = ""; 
    
    private static Connection connection = null;

    private DBConnection() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("org.h2.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("✅ SUCCESS: CloudCost Database connected!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ ERROR: h2.jar is missing from your classpath.");
        } catch (SQLException e) {
            System.err.println("❌ ERROR: Database connection failed.");
            e.printStackTrace();
        }
        return connection;
    }

    public static void main(String[] args) {
        getConnection(); 
    }
}
