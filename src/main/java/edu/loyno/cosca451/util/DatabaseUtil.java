 /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * Filename: DatabaseUtil.java                                                      *
 * Project: Infrastructure Reporting & Tracking System                              *
 * Description: Provides centralized database connection management and utility     *
 *              methods for interacting with the application’s data source.         *
 * Author: Sophina Nichols                                                          *
 * Edited By:                                                                       *
 * Hector Maes - 04/02/2026                                                         *
 * Date Last Modified: 04/02/2026                                                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.loyno.cosca451.util;

import org.postgresql.ds.PGSimpleDataSource;
import javax.sql.DataSource;

public class DatabaseUtil {

    private static PGSimpleDataSource dataSource;

    static {
        dataSource = new PGSimpleDataSource();

        String host     = System.getenv("DB_HOST")     != null ? System.getenv("DB_HOST")     : "localhost";
        String port     = System.getenv("DB_PORT")     != null ? System.getenv("DB_PORT")     : "5432";
        String dbName   = System.getenv("DB_NAME")     != null ? System.getenv("DB_NAME")     : "nola_db";
        String user     = System.getenv("DB_USER")     != null ? System.getenv("DB_USER")     : "postgres";
        String password = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "nola2345";

        dataSource.setServerNames(new String[]{host});
        dataSource.setPortNumbers(new int[]{Integer.parseInt(port)});
        dataSource.setDatabaseName(dbName);
        dataSource.setUser(user);
        dataSource.setPassword(password);
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}