package at.fhtw.swen1.mrp;

import at.fhtw.swen1.mrp.server.HttpServer;

public class Main {
    public static void main(String[] args) {
        try {
            HttpServer server = new HttpServer();
            server.start();
            System.out.println("Media Ratings Platform server is running on http://localhost:8080");
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
