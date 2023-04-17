package com.example.oblakscraper.sqlite;

public class DDLQueriesStrings {
    public static final String CREATE_TABLE_WEBSITES =
        """
            CREATE TABLE IF NOT EXISTS Websites (
                id TEXT PRIMARY KEY,
                owner_id TEXT NOT NULL,
                title TEXT,
                url TEXT NOT NULL,
                text TEXT
            );
        """;

    public static final String CREATE_TABLE_IMAGES =
        """
            CREATE TABLE IF NOT EXISTS Images (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                website_id TEXT NOT NULL,
                url TEXT NOT NULL,
                alt TEXT,
                
                FOREIGN KEY (website_id) REFERENCES Websites (id) ON DELETE CASCADE
            );
        """;

    public static final String DELETE_DATABASE =
        """
            DROP TABLE IF EXISTS Websites;
            DROP TABLE IF EXISTS Images;
        """;
}
