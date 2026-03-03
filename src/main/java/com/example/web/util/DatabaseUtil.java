package com.example.web.util;

import org.postgresql.ds.PGSimpleDataSource;
import javax.sql.DataSource;

public class DatabaseUtil {

    private static PGSimpleDataSource dataSource;

    static {
        dataSource = new PGSimpleDataSource();
        dataSource.setServerNames(new String[]{"localhost"});
        dataSource.setPortNumbers(new int[]{5432});
        dataSource.setDatabaseName("nola_db");
        dataSource.setUser("postgres");
        dataSource.setPassword("");
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}