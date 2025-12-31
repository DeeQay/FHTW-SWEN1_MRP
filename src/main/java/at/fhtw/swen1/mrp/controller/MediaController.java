package at.fhtw.swen1.mrp.controller;

import at.fhtw.swen1.mrp.dto.request.MediaRequest;
import at.fhtw.swen1.mrp.dto.response.MediaResponse;
import at.fhtw.swen1.mrp.service.MediaService;
import at.fhtw.swen1.mrp.service.AuthService;
import at.fhtw.swen1.mrp.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * MediaController verwaltet media-bezogene HTTP Endpoints
 */
public class MediaController {
    private final MediaService mediaService;
    private final AuthService authService;

    public MediaController() {
        this.mediaService = new MediaService();
        this.authService = new AuthService();
    }

    public void handleMedia(HttpExchange exchange) throws IOException {
        // Prüfe Authentication für alle Media-Endpoints
        if (!isAuthenticated(exchange)) {
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized - Missing or invalid token\"}");
            return;
        }
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        // Route basierend auf Method und Path
        if ("/api/media".equals(path) || path.equals("/api/media/")) {
            switch (method) {
                case "POST" -> handleCreateMedia(exchange);
                case "GET" -> handleGetAllMedia(exchange);
                default -> sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        } else if (path.matches("/api/media/\\d+")) {
            // Media ID aus Pfad extrahieren
            String[] pathParts = path.split("/");
            String mediaId = pathParts[pathParts.length - 1];

            switch (method) {
                case "GET" -> handleGetMedia(exchange, mediaId);
                case "PUT" -> handleUpdateMedia(exchange, mediaId);
                case "DELETE" -> handleDeleteMedia(exchange, mediaId);
                default -> sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Endpoint not found\"}");
        }
    }

    private void handleCreateMedia(HttpExchange exchange) throws IOException {
        try {
            String requestBody = readRequestBody(exchange);
            MediaRequest request = JsonUtil.fromJson(requestBody, MediaRequest.class);

            at.fhtw.swen1.mrp.entity.Media media = mediaService.createMedia(
                    request.getTitle(),
                    request.getDescription(),
                    request.getMediaType(),
                    request.getReleaseYear(),
                    request.getGenres(),
                    request.getAgeRestriction()
            );

            MediaResponse response = new MediaResponse(
                    media.getId(),
                    media.getTitle(),
                    media.getDescription(),
                    media.getMediaType(),
                    media.getReleaseYear(),
                    media.getGenres(),
                    media.getAgeRestriction(),
                    media.getCreatedAt()
            );

            sendResponse(exchange, 201, JsonUtil.toJson(response));
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid request: " + e.getMessage() + "\"}");
        }
    }

    private void handleGetAllMedia(HttpExchange exchange) throws IOException {
        try {
            List<at.fhtw.swen1.mrp.entity.Media> mediaList = mediaService.getAllMedia();

            List<MediaResponse> responseList = mediaList.stream()
                    .map(m -> new MediaResponse(m.getId(), m.getTitle(), m.getDescription(),
                            m.getMediaType(), m.getReleaseYear(), m.getGenres(), m.getAgeRestriction(), m.getCreatedAt()))
                    .collect(java.util.stream.Collectors.toList());

            sendResponse(exchange, 200, JsonUtil.toJson(responseList));
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }

    private void handleGetMedia(HttpExchange exchange, String mediaId) throws IOException {
        try {
            at.fhtw.swen1.mrp.entity.Media media = mediaService.getMediaById(Long.parseLong(mediaId));

            MediaResponse response = new MediaResponse(
                    media.getId(),
                    media.getTitle(),
                    media.getDescription(),
                    media.getMediaType(),
                    media.getReleaseYear(),
                    media.getGenres(),
                    media.getAgeRestriction(),
                    media.getCreatedAt()
            );

            sendResponse(exchange, 200, JsonUtil.toJson(response));
        } catch (Exception e) {
            sendResponse(exchange, 404, "{\"error\":\"Media not found\"}");
        }
    }

    private void handleUpdateMedia(HttpExchange exchange, String mediaId) throws IOException {
        try {
            String requestBody = readRequestBody(exchange);
            MediaRequest request = JsonUtil.fromJson(requestBody, MediaRequest.class);

            at.fhtw.swen1.mrp.entity.Media media = mediaService.updateMedia(
                    Long.parseLong(mediaId),
                    request.getTitle(),
                    request.getDescription(),
                    request.getMediaType(),
                    request.getReleaseYear(),
                    request.getGenres(),
                    request.getAgeRestriction()
            );

            MediaResponse response = new MediaResponse(
                    media.getId(),
                    media.getTitle(),
                    media.getDescription(),
                    media.getMediaType(),
                    media.getReleaseYear(),
                    media.getGenres(),
                    media.getAgeRestriction(),
                    media.getCreatedAt()
            );

            sendResponse(exchange, 200, JsonUtil.toJson(response));
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 404, "{\"error\":\"Media not found\"}");
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\":\"Update failed: " + e.getMessage() + "\"}");
        }
    }

    private void handleDeleteMedia(HttpExchange exchange, String mediaId) throws IOException {
        try {
            mediaService.deleteMedia(Long.parseLong(mediaId));
            sendResponse(exchange, 204, "");
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 404, "{\"error\":\"Media not found\"}");
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\":\"Delete failed\"}");
        }
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

    private boolean isAuthenticated(HttpExchange exchange) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }

        String token = authHeader.substring(7); // "Bearer " prefix entfernen
        return authService.validateToken(token);
    }
}

