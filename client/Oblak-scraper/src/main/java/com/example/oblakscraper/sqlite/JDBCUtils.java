package com.example.oblakscraper.sqlite;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class JDBCUtils {
    public static Connection getConnection() throws ClassNotFoundException, SQLException, URISyntaxException {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection("jdbc:sqlite:oblak-scraper.db");
    }

    public static void createDatabaseStructure() throws SQLException, URISyntaxException, ClassNotFoundException {
        try (Connection connection = JDBCUtils.getConnection()) {
            Statement statement = connection.createStatement();

            statement.execute(DDLQueriesStrings.CREATE_TABLE_WEBSITES);
            statement.execute(DDLQueriesStrings.CREATE_TABLE_IMAGES);
        } catch (SQLException e) {
            throw e;
        }
    }

    public static void deleteDatabase() throws SQLException, URISyntaxException, ClassNotFoundException {
        try (Connection connection = JDBCUtils.getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute(DDLQueriesStrings.DELETE_DATABASE);
        } catch (SQLException e) {
            throw e;
        }
    }
}
