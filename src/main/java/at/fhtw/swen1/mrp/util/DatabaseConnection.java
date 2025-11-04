package at.fhtw.swen1.mrp.util;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DatabaseConnection {
    private static String dbUrl;
    private static String dbUsername;
    private static String dbPassword;

    static {
        try {
            // PostgreSQL-Treiber explizit laden
            Class.forName("org.postgresql.Driver");

            // DB-Konfiguration laden
            Properties props = new Properties();
            InputStream input = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("application.properties");

            if (input == null) {
                throw new RuntimeException("application.properties nicht gefunden!");
            }

            props.load(input);
            dbUrl = props.getProperty("db.url");
            dbUsername = props.getProperty("db.username");
            dbPassword = props.getProperty("db.password");
            input.close();

            System.out.println("Datenbank-Konfiguration geladen");
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Laden der DB-Konfiguration: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() {
        try {
            // Erstelle NEUE Connection für jeden Request
            Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            return conn;
        } catch (Exception e) {
            throw new RuntimeException("Datenbankverbindung fehlgeschlagen: " + e.getMessage(), e);
        }
    }
}
