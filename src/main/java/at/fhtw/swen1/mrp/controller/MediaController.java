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
 * Zwischenabgabe - grundlegende CRUD Operationen für Media
 */
public class MediaController {
    private final MediaService mediaService;
    private final AuthService authService;

    public MediaController() {
        // TODO: Mit ordnungsgemäßer Dependency Injection in final submission initialisieren
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
        if ("/api/media".equals(path)) {
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

            // TODO: Media-Erstellung

            // Stub implementation
            MediaResponse response = new MediaResponse(123L, request.getTitle(), request.getDescription(),
                request.getMediaType(), request.getReleaseYear(), request.getGenres(), request.getAgeRestriction(), null);

            sendResponse(exchange, 201, JsonUtil.toJson(response));
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid request: " + e.getMessage() + "\"}");
        }
    }

    private void handleGetAllMedia(HttpExchange exchange) throws IOException {
        try {
            // TODO: Media-Abruf aus Database

            // Stub implementation
            List<MediaResponse> mediaList = List.of(); // Leere Liste für intermediate
            sendResponse(exchange, 200, JsonUtil.toJson(mediaList));
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }

    private void handleGetMedia(HttpExchange exchange, String mediaId) throws IOException {
        try {
            // TODO: Media nach ID aus Database abrufen
            // TODO: Media not found Fälle behandeln

            // Stub implementation
            MediaResponse response = new MediaResponse(Long.parseLong(mediaId), "Sample Movie",
                "Sample Description", "Movie", 2024, null, null, null);

            sendResponse(exchange, 200, JsonUtil.toJson(response));
        } catch (Exception e) {
            sendResponse(exchange, 404, "{\"error\":\"Media not found\"}");
        }
    }

    private void handleUpdateMedia(HttpExchange exchange, String mediaId) throws IOException {
        try {
            String requestBody = readRequestBody(exchange);
            MediaRequest request = JsonUtil.fromJson(requestBody, MediaRequest.class);

            // TODO: Media Update in Database implementieren
            // TODO: Authorization prüfen (nur Owner kann updaten)

            // Stub implementation
            MediaResponse response = new MediaResponse(Long.parseLong(mediaId), request.getTitle(),
                request.getDescription(), request.getMediaType(), request.getReleaseYear(), request.getGenres(),
                request.getAgeRestriction(), null);

            sendResponse(exchange, 200, JsonUtil.toJson(response));
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\":\"Update failed: " + e.getMessage() + "\"}");
        }
    }

    private void handleDeleteMedia(HttpExchange exchange, String mediaId) throws IOException {
        try {
            // TODO: Media Delete aus Database implementieren
            // TODO: Authorization prüfen (nur Owner kann löschen)

            // Stub implementation
            sendResponse(exchange, 204, "");
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
        String authHeader = exchange.getRequestHeaders().getFirst("Authentication");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }

        String token = authHeader.substring(7); // "Bearer " prefix entfernen
        return authService.validateToken(token);
    }
}

