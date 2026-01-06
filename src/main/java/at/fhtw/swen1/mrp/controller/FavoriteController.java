package at.fhtw.swen1.mrp.controller;

import at.fhtw.swen1.mrp.dto.response.FavoriteResponse;
import at.fhtw.swen1.mrp.entity.Favorite;
import at.fhtw.swen1.mrp.entity.User;
import at.fhtw.swen1.mrp.service.AuthService;
import at.fhtw.swen1.mrp.service.FavoriteService;
import at.fhtw.swen1.mrp.service.UserService;
import at.fhtw.swen1.mrp.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Controller für Favorite-bezogene HTTP Endpoints
 */
public class FavoriteController {
    private final FavoriteService favoriteService;
    private final AuthService authService;
    private final UserService userService;

    public FavoriteController() {
        this.favoriteService = new FavoriteService();
        this.authService = new AuthService();
        this.userService = new UserService();
    }

    public void handleFavorite(HttpExchange exchange) throws IOException {
        // Authentication prüfen
        if (!isAuthenticated(exchange)) {
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized\"}");
            return;
        }

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        // POST /api/media/{mediaId}/favorite - Als Favorit markieren
        if (path.matches("/api/media/\\d+/favorite") && "POST".equals(method)) {
            String mediaId = extractPathSegment(path, 3);
            handleAddFavorite(exchange, mediaId);
            return;
        }

        // DELETE /api/media/{mediaId}/favorite - Favorit entfernen
        if (path.matches("/api/media/\\d+/favorite") && "DELETE".equals(method)) {
            String mediaId = extractPathSegment(path, 3);
            handleRemoveFavorite(exchange, mediaId);
            return;
        }

        // GET /api/users/{username}/favorites - Favoriten-Liste
        if (path.matches("/api/users/[^/]+/favorites") && "GET".equals(method)) {
            String username = extractPathSegment(path, 3);
            handleGetFavorites(exchange, username);
            return;
        }

        sendResponse(exchange, 404, "{\"error\":\"Endpoint not found\"}");
    }

    // Media als Favorit hinzufügen
    private void handleAddFavorite(HttpExchange exchange, String mediaIdStr) throws IOException {
        try {
            Long mediaId = Long.parseLong(mediaIdStr);
            Long userId = getUserIdFromToken(exchange);

            Favorite favorite = favoriteService.addFavorite(userId, mediaId);
            sendResponse(exchange, 201, JsonUtil.toJson(mapToResponse(favorite)));

        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\":\"Ungültige Media ID\"}");
        } catch (IllegalStateException e) {
            // Token-Problem oder bereits als Favorit markiert
            if (e.getMessage().contains("Token") || e.getMessage().contains("User nicht gefunden")) {
                sendResponse(exchange, 401, "{\"error\":\"" + e.getMessage() + "\"}");
            } else {
                sendResponse(exchange, 409, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        } catch (IllegalArgumentException e) {
            // Media nicht gefunden
            sendResponse(exchange, 404, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"Interner Server-Fehler\"}");
        }
    }

    // Favorit entfernen
    private void handleRemoveFavorite(HttpExchange exchange, String mediaIdStr) throws IOException {
        try {
            Long mediaId = Long.parseLong(mediaIdStr);
            Long userId = getUserIdFromToken(exchange);

            favoriteService.removeFavorite(userId, mediaId);
            sendResponse(exchange, 204, "");

        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\":\"Ungültige Media ID\"}");
        } catch (IllegalStateException e) {
            // Token-Problem oder nicht als Favorit markiert
            if (e.getMessage().contains("Token") || e.getMessage().contains("User nicht gefunden")) {
                sendResponse(exchange, 401, "{\"error\":\"" + e.getMessage() + "\"}");
            } else {
                sendResponse(exchange, 404, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"Interner Server-Fehler\"}");
        }
    }

    // Favoriten-Liste eines Users abrufen
    private void handleGetFavorites(HttpExchange exchange, String username) throws IOException {
        try {
            User user = userService.getUserByUsername(username);
            if (user == null) {
                sendResponse(exchange, 404, "{\"error\":\"User nicht gefunden\"}");
                return;
            }

            List<Favorite> favorites = favoriteService.getFavoritesByUserId(user.getId());
            List<FavoriteResponse> response = favorites.stream()
                    .map(this::mapToResponse)
                    .toList();

            sendResponse(exchange, 200, JsonUtil.toJson(response));

        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"Interner Server-Fehler\"}");
        }
    }

    // Favorite zu Response DTO
    private FavoriteResponse mapToResponse(Favorite favorite) {
        return new FavoriteResponse(
                favorite.getId(),
                favorite.getUserId(),
                favorite.getMediaId(),
                favorite.getCreatedAt()
        );
    }

    // UserId aus Token extrahieren
    private Long getUserIdFromToken(HttpExchange exchange) {
        String token = getTokenFromHeader(exchange);
        String username = authService.getUsernameFromToken(token);
        if (username == null) {
            throw new IllegalStateException("Ungültiger Token");
        }
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new IllegalStateException("User nicht gefunden");
        }
        return user.getId();
    }

    private String getTokenFromHeader(HttpExchange exchange) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing or invalid Authorization header");
        }
        return authHeader.substring(7);
    }

    private boolean isAuthenticated(HttpExchange exchange) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }
        String token = authHeader.substring(7);
        return authService.validateToken(token);
    }

    private String extractPathSegment(String path, int index) {
        String[] parts = path.split("/");
        return parts[index];
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseBytes.length == 0 ? -1 : responseBytes.length);

        if (responseBytes.length > 0) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
    }
}

