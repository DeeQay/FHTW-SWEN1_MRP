package at.fhtw.swen1.mrp.controller;

import at.fhtw.swen1.mrp.dto.request.RatingRequest;
import at.fhtw.swen1.mrp.dto.response.RatingResponse;
import at.fhtw.swen1.mrp.entity.Rating;
import at.fhtw.swen1.mrp.entity.User;
import at.fhtw.swen1.mrp.service.AuthService;
import at.fhtw.swen1.mrp.service.RatingService;
import at.fhtw.swen1.mrp.service.UserService;
import at.fhtw.swen1.mrp.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Controller für Rating-bezogene HTTP Endpoints
 */
public class RatingController {
    private final RatingService ratingService;
    private final AuthService authService;
    private final UserService userService;

    public RatingController() {
        this.ratingService = new RatingService();
        this.authService = new AuthService();
        this.userService = new UserService();
    }

    public void handleRating(HttpExchange exchange) throws IOException {
        // Authentication prüfen
        if (!isAuthenticated(exchange)) {
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized\"}");
            return;
        }

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        // POST /api/media/{mediaId}/rate - Rating erstellen
        if (path.matches("/api/media/\\d+/rate") && "POST".equals(method)) {
            String mediaId = extractPathSegment(path, 3);
            handleCreateRating(exchange, mediaId);
            return;
        }

        // PUT /api/ratings/{ratingId} - Rating bearbeiten
        if (path.matches("/api/ratings/\\d+") && "PUT".equals(method)) {
            String ratingId = extractPathSegment(path, 3);
            handleUpdateRating(exchange, ratingId);
            return;
        }

        // DELETE /api/ratings/{ratingId} - Rating löschen
        if (path.matches("/api/ratings/\\d+") && "DELETE".equals(method)) {
            String ratingId = extractPathSegment(path, 3);
            handleDeleteRating(exchange, ratingId);
            return;
        }

        // POST /api/ratings/{ratingId}/confirm - Kommentar bestätigen
        if (path.matches("/api/ratings/\\d+/confirm") && "POST".equals(method)) {
            String ratingId = extractPathSegment(path, 3);
            handleConfirmComment(exchange, ratingId);
            return;
        }

        // POST /api/ratings/{ratingId}/like - Rating liken
        if (path.matches("/api/ratings/\\d+/like") && "POST".equals(method)) {
            String ratingId = extractPathSegment(path, 3);
            handleLikeRating(exchange, ratingId);
            return;
        }

        // DELETE /api/ratings/{ratingId}/like - Rating unliken
        if (path.matches("/api/ratings/\\d+/like") && "DELETE".equals(method)) {
            String ratingId = extractPathSegment(path, 3);
            handleUnlikeRating(exchange, ratingId);
            return;
        }

        sendResponse(exchange, 404, "{\"error\":\"Endpoint not found\"}");
    }

    private void handleCreateRating(HttpExchange exchange, String mediaIdStr) throws IOException {
        try {
            Long mediaId = Long.parseLong(mediaIdStr);
            Long userId = getUserIdFromToken(exchange);

            String requestBody = readRequestBody(exchange);
            RatingRequest request = JsonUtil.fromJson(requestBody, RatingRequest.class);

            // Score Validierung
            Integer score = request.getScore();
            if (score == null) {
                sendResponse(exchange, 400, "{\"error\":\"Score darf nicht leer sein\"}");
                return;
            }
            if (score < 1 || score > 5) {
                sendResponse(exchange, 400, "{\"error\":\"Score muss zwischen 1 und 5 liegen\"}");
                return;
            }

            Rating rating = ratingService.createRating(userId, mediaId, score, request.getComment());
            sendResponse(exchange, 201, JsonUtil.toJson(mapToResponse(rating)));

        } catch (IllegalStateException e) {
            // User hat bereits bewertet
            sendResponse(exchange, 409, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void handleUpdateRating(HttpExchange exchange, String ratingIdStr) throws IOException {
        try {
            Long ratingId = Long.parseLong(ratingIdStr);
            Long userId = getUserIdFromToken(exchange);

            String requestBody = readRequestBody(exchange);
            RatingRequest request = JsonUtil.fromJson(requestBody, RatingRequest.class);

            // Score Validierung
            Integer score = request.getScore();
            if (score == null) {
                sendResponse(exchange, 400, "{\"error\":\"Score darf nicht leer sein\"}");
                return;
            }
            if (score < 1 || score > 5) {
                sendResponse(exchange, 400, "{\"error\":\"Score muss zwischen 1 und 5 liegen\"}");
                return;
            }

            Rating rating = ratingService.updateRating(ratingId, userId, score, request.getComment());
            sendResponse(exchange, 200, JsonUtil.toJson(mapToResponse(rating)));

        } catch (SecurityException e) {
            sendResponse(exchange, 403, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 404, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void handleDeleteRating(HttpExchange exchange, String ratingIdStr) throws IOException {
        try {
            Long ratingId = Long.parseLong(ratingIdStr);
            Long userId = getUserIdFromToken(exchange);

            ratingService.deleteRating(ratingId, userId);
            sendResponse(exchange, 204, "");

        } catch (SecurityException e) {
            sendResponse(exchange, 403, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 404, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void handleConfirmComment(HttpExchange exchange, String ratingIdStr) throws IOException {
        try {
            Long ratingId = Long.parseLong(ratingIdStr);
            Long userId = getUserIdFromToken(exchange);

            Rating rating = ratingService.confirmComment(ratingId, userId);
            sendResponse(exchange, 200, JsonUtil.toJson(mapToResponse(rating)));

        } catch (SecurityException e) {
            sendResponse(exchange, 403, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 404, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void handleLikeRating(HttpExchange exchange, String ratingIdStr) throws IOException {
        try {
            Long ratingId = Long.parseLong(ratingIdStr);
            Long userId = getUserIdFromToken(exchange);

            Rating rating = ratingService.likeRating(ratingId, userId);
            sendResponse(exchange, 200, JsonUtil.toJson(mapToResponse(rating)));

        } catch (IllegalStateException e) {
            // User hat bereits geliked oder eigenes Rating
            sendResponse(exchange, 409, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 404, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void handleUnlikeRating(HttpExchange exchange, String ratingIdStr) throws IOException {
        try {
            Long ratingId = Long.parseLong(ratingIdStr);
            Long userId = getUserIdFromToken(exchange);

            Rating rating = ratingService.unlikeRating(ratingId, userId);
            sendResponse(exchange, 200, JsonUtil.toJson(mapToResponse(rating)));

        } catch (IllegalStateException e) {
            // User hat nicht geliked
            sendResponse(exchange, 409, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 404, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // Rating zu Response DTO
    private RatingResponse mapToResponse(Rating rating) {
        return new RatingResponse(
                rating.getId(),
                rating.getUserId(),
                rating.getMediaId(),
                rating.getScore(),
                rating.getComment(),
                rating.getIsConfirmed(),
                rating.getLikeCount(),
                rating.getCreatedAt(),
                rating.getUpdatedAt()
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
        // prüft, ob Authorization-Header vorhanden und im "Bearer" Format ist
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing or invalid Authorization header");
        }
        return authHeader.substring(7); // "Bearer " prefix entfernen
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

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
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

