package at.fhtw.swen1.mrp.controller;

import at.fhtw.swen1.mrp.dto.request.ProfileUpdateRequest;
import at.fhtw.swen1.mrp.dto.response.LeaderboardEntryResponse;
import at.fhtw.swen1.mrp.dto.response.UserProfileResponse;
import at.fhtw.swen1.mrp.dto.response.UserStatisticsResponse;
import at.fhtw.swen1.mrp.entity.Media;
import at.fhtw.swen1.mrp.entity.Rating;
import at.fhtw.swen1.mrp.entity.User;
import at.fhtw.swen1.mrp.service.AuthService;
import at.fhtw.swen1.mrp.service.RecommendationService;
import at.fhtw.swen1.mrp.service.UserService;
import at.fhtw.swen1.mrp.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class UserController {
    private final UserService userService;
    private final AuthService authService;
    private final RecommendationService recommendationService;

    public UserController() {
        this.userService = new UserService();
        this.authService = new AuthService();
        this.recommendationService = new RecommendationService();
    }

    public void handleUser(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        // Check if path matches /api/users/{username}/profile
        if (path.matches("/api/users/[^/]+/profile")) {
            if ("GET".equals(method)) {
                handleGetProfile(exchange);
            } else if ("PUT".equals(method)) {
                handlePutProfile(exchange);
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        } else if (path.matches("/api/users/[^/]+/ratings")) {
            handleGetRatings(exchange);
        } else if (path.matches("/api/users/[^/]+/recommendations")) {
            handleGetRecommendations(exchange);
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Endpoint not found\"}");
        }
    }

    private void handleGetProfile(HttpExchange exchange) throws IOException {
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

            // Statistics laden
            UserStatisticsResponse statistics = userService.getUserStatistics(user.getId());

            // Profile Response mit Statistics erstellen
            UserProfileResponse profile = new UserProfileResponse(
                    user.getUsername(),
                    user.getEmail() != null ? user.getEmail() : "",
                    user.getCreatedAt(),
                    statistics
            );

            String response = JsonUtil.toJson(profile);
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }

    // Profile (Email) aktualisieren
    private void handlePutProfile(HttpExchange exchange) throws IOException {
        if (!isAuthenticated(exchange)) {
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized\"}");
            return;
        }

        try {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");

            // Validiere Path-Format
            if (parts.length < 4) {
                sendResponse(exchange, 400, "{\"error\":\"Invalid path\"}");
                return;
            }

            String username = parts[3]; // /api/users/{username}/profile

            // User aus Token extrahieren
            String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
            String token = authHeader.substring(7);

            // Request Body lesen
            String requestBody = readRequestBody(exchange);
            ProfileUpdateRequest request = JsonUtil.fromJson(requestBody, ProfileUpdateRequest.class);

            // Validiere Email vorhanden
            if (request == null || request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                sendResponse(exchange, 400, "{\"error\":\"Email is required\"}");
                return;
            }

            String requestingUsername = authService.getUsernameFromToken(token);
            // Ownership check
            if (!requestingUsername.equals(username)) {
                sendResponse(exchange, 403, "{\"error\":\"You can only edit your own profile\"}");
                return;
            }

            User updatedUser = userService.updateUserProfile(username, request.getEmail());

            // Updated Profile Response
            UserStatisticsResponse statistics = userService.getUserStatistics(updatedUser.getId());
            UserProfileResponse profile = new UserProfileResponse(
                    updatedUser.getUsername(),
                    updatedUser.getEmail() != null ? updatedUser.getEmail() : "",
                    updatedUser.getCreatedAt(),
                    statistics
            );

            String response = JsonUtil.toJson(profile);
            sendResponse(exchange, 200, response);
        } catch (IllegalArgumentException e) {
            String errorMsg = e.getMessage() != null ? e.getMessage().replace("\"", "'") : "Invalid request";
            sendResponse(exchange, 403, "{\"error\":\"" + errorMsg + "\"}");
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }

    // Rating History eines Users abrufen
    private void handleGetRatings(HttpExchange exchange) throws IOException {
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
            String username = parts[3]; // /api/users/{username}/ratings

            User user = userService.getUserByUsername(username);
            if (user == null) {
                sendResponse(exchange, 404, "{\"error\":\"User not found\"}");
                return;
            }

            List<Rating> ratings = userService.getUserRatings(user.getId());
            String response = JsonUtil.toJson(ratings);
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }

    // Recommendations: Genre-basierte Empfehlungen
    private void handleGetRecommendations(HttpExchange exchange) throws IOException {
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
            String username = parts[3]; // /api/users/{username}/recommendations

            User user = userService.getUserByUsername(username);
            if (user == null) {
                sendResponse(exchange, 404, "{\"error\":\"User not found\"}");
                return;
            }

            List<Media> recommendations = recommendationService.getRecommendationsByGenre(user.getId(), 10);
            String response = JsonUtil.toJson(recommendations);
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }

    // Leaderboard: Top 10 User nach Anzahl Ratings
    public void handleLeaderboard(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }

        if (!isAuthenticated(exchange)) {
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized\"}");
            return;
        }

        try {
            List<LeaderboardEntryResponse> leaderboard = userService.getLeaderboard(10);
            String response = JsonUtil.toJson(leaderboard);
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

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
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
