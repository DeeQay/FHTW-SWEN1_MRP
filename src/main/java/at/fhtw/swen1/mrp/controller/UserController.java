package at.fhtw.swen1.mrp.controller;

import at.fhtw.swen1.mrp.entity.User;
import at.fhtw.swen1.mrp.service.AuthService;
import at.fhtw.swen1.mrp.service.UserService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class UserController {
    private final UserService userService;
    private final AuthService authService;

    public UserController() {
        this.userService = new UserService();
        this.authService = new AuthService();
    }

    public void handleUser(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        // Check if path matches /api/users/{username}/profile
        if (path.matches("/api/users/[^/]+/profile")) {
            handleGetProfile(exchange);
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Endpoint not found\"}");
        }
    }

    private void handleGetProfile(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }

        if (!isAuthenticated(exchange)) {
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized\"}");
            return;
        }

        try {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            String username = parts[3]; // /api/users/{username}/profile

            User user = userService.getUserByUsername(username);
            if (user == null) {
                sendResponse(exchange, 404, "{\"error\":\"User not found\"}");
                return;
            }

            String response = String.format(
                "{\"username\":\"%s\",\"email\":\"%s\",\"createdAt\":\"%s\"}",
                user.getUsername(),
                user.getEmail() != null ? user.getEmail() : "",
                user.getCreatedAt()
            );

            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }

    private boolean isAuthenticated(HttpExchange exchange) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }
        String token = authHeader.substring(7);
        return authService.validateToken(token);
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
