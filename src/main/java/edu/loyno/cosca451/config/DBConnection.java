
 /**************************************************************************
 * Filename: DBConnection.java
 * Project: Infrastructure Reporting & Tracking System
 * Description: This file manages the connection between the Java application and the PostgreSQL database.
 *              Instead of creating a new database connection every time one is needed, this class reuses
 *              a single connection (Singleton pattern) to keep things efficient. It reads database credentials
 *              from environment variables so that sensitive information like passwords are never hardcoded into the source code.
 * Author: Sophina Nichols
 * Edited By:
 * Hector Maes - 04/02/2026
 * Date Last Modified: 04/02/2026
 **************************************************************************/

package edu.loyno.cosca451.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static Connection connection = null; // Holds the single shared connection instance

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = System.getenv("DB_URL");
            String user = System.getenv("DB_USER");
            String password = System.getenv("DB_PASSWORD");

            if (url == null || user == null || password == null) {
                throw new SQLException("Missing database environment variables");
            }

            connection = DriverManager.getConnection(url, user, password);
        }
        return connection;
    } 
}
