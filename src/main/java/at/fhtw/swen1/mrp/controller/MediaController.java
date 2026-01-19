package at.fhtw.swen1.mrp.controller;

import at.fhtw.swen1.mrp.dto.request.MediaRequest;
import at.fhtw.swen1.mrp.dto.response.MediaRatingsResponse;
import at.fhtw.swen1.mrp.dto.response.MediaResponse;
import at.fhtw.swen1.mrp.dto.response.RatingResponse;
import at.fhtw.swen1.mrp.entity.Rating;
import at.fhtw.swen1.mrp.service.MediaService;
import at.fhtw.swen1.mrp.service.AuthService;
import at.fhtw.swen1.mrp.service.RatingService;
import at.fhtw.swen1.mrp.service.UserService;
import at.fhtw.swen1.mrp.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MediaController verwaltet media-bezogene HTTP Endpoints
 */
public class MediaController {
    private final MediaService mediaService;
    private final RatingService ratingService;
    private final AuthService authService;
    private final UserService userService;

    public MediaController() {
        this.mediaService = new MediaService();
        this.ratingService = new RatingService();
        this.authService = new AuthService();
        this.userService = new UserService();
    }

    public void handleMedia(HttpExchange exchange) throws IOException {
        // Prüfe Authentication für alle Media-Endpoints
        if (!isAuthenticated(exchange)) {
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized\"}");
            return;
        }
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        // Route basierend auf Method und Path
        if ("/api/media".equals(path) || "/api/media/".equals(path)) {
            switch (method) {
                case "POST" -> handleCreateMedia(exchange);
                case "GET" -> handleGetAllMedia(exchange);
                default -> sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        } else if (path.matches("/api/media/\\d+/ratings")) {
            // GET /api/media/{mediaId}/ratings - Ratings mit Average abrufen
            if ("GET".equals(method)) {
                String[] pathParts = path.split("/");
                String mediaId = pathParts[3];
                handleGetMediaRatings(exchange, mediaId);
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
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
            Long userId = getUserIdFromToken(exchange);
            String requestBody = readRequestBody(exchange);
            MediaRequest request = JsonUtil.fromJson(requestBody, MediaRequest.class);

            at.fhtw.swen1.mrp.entity.Media media = mediaService.createMedia(
                    request.getTitle(),
                    request.getDescription(),
                    request.getMediaType(),
                    request.getReleaseYear(),
                    request.getGenres(),
                    request.getAgeRestriction(),
                    userId
            );

            MediaResponse response = new MediaResponse(
                    media.getId(),
                    media.getTitle(),
                    media.getDescription(),
                    media.getMediaType(),
                    media.getReleaseYear(),
                    media.getGenres(),
                    media.getAgeRestriction(),
                    media.getCreatorId(),
                    media.getCreatedAt()
            );

            sendResponse(exchange, 201, JsonUtil.toJson(response));
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid request: " + e.getMessage() + "\"}");
        }
    }

    private void handleGetAllMedia(HttpExchange exchange) throws IOException {
        try {
            // Query-Parameter extrahieren
            Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());

            String title = queryParams.get("title");
            String genre = queryParams.get("genre");
            String mediaType = queryParams.get("mediaType");
            String ageRestriction = queryParams.get("ageRestriction");
            String sortBy = queryParams.get("sortBy");

            // Integer Parameter parsen
            Integer releaseYear = null;
            if (queryParams.containsKey("releaseYear")) {
                try {
                    releaseYear = Integer.parseInt(queryParams.get("releaseYear"));
                } catch (NumberFormatException ignored) {}
            }

            // Rating Parameter parsen (minRating)
            Double minRating = null;
            if (queryParams.containsKey("rating")) {
                try {
                    minRating = Double.parseDouble(queryParams.get("rating"));
                } catch (NumberFormatException ignored) {}
            }

            // Entscheide ob Filter aktiv sind
            boolean hasFilters = title != null || genre != null || mediaType != null ||
                                 releaseYear != null || ageRestriction != null ||
                                 minRating != null || sortBy != null;

            List<at.fhtw.swen1.mrp.entity.Media> mediaList;
            if (hasFilters) {
                mediaList = mediaService.searchMedia(title, genre, mediaType, releaseYear,
                                                      ageRestriction, minRating, sortBy);
            } else {
                mediaList = mediaService.getAllMedia();
            }

            List<MediaResponse> responseList = mediaList.stream()
                    .map(m -> new MediaResponse(m.getId(), m.getTitle(), m.getDescription(),
                            m.getMediaType(), m.getReleaseYear(), m.getGenres(), m.getAgeRestriction(), m.getCreatorId(), m.getCreatedAt()))
                    .collect(java.util.stream.Collectors.toList());

            sendResponse(exchange, 200, JsonUtil.toJson(responseList));
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }


    // Parst Query-String in Map.
    // Bsp: "title=Matrix&genre=Action" -> {title: Matrix, genre: Action}
    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isBlank()) {
            return params;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
        return params;
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
                    media.getCreatorId(),
                    media.getCreatedAt()
            );

            sendResponse(exchange, 200, JsonUtil.toJson(response));
        } catch (Exception e) {
            sendResponse(exchange, 404, "{\"error\":\"Media not found\"}");
        }
    }


     // Gibt Ratings mit Average Score zurück, nur confirmed sichtbar
    private void handleGetMediaRatings(HttpExchange exchange, String mediaIdStr) throws IOException {
        try {
            Long mediaId = Long.parseLong(mediaIdStr);
            Long userId = getUserIdFromToken(exchange);

            // Ratings mit Kommentar-Filter
            List<Rating> ratings = ratingService.getRatingsByMediaIdPublic(mediaId, userId);

            Double averageRating = ratingService.calculateAverageRating(mediaId);

            // Response DTO erstellen
            List<RatingResponse> ratingResponses = ratings.stream()
                    .map(r -> new RatingResponse(
                            r.getId(),
                            r.getUserId(),
                            r.getMediaId(),
                            r.getScore(),
                            r.getComment(),
                            r.getIsConfirmed(),
                            r.getLikeCount(),
                            r.getCreatedAt(),
                            r.getUpdatedAt()
                    ))
                    .collect(java.util.stream.Collectors.toList());

            MediaRatingsResponse response = new MediaRatingsResponse(
                    averageRating,
                    ratings.size(),
                    ratingResponses
            );

            sendResponse(exchange, 200, JsonUtil.toJson(response));
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid media ID\"}");
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }

    private void handleUpdateMedia(HttpExchange exchange, String mediaId) throws IOException {
        try {
            Long userId = getUserIdFromToken(exchange);
            String requestBody = readRequestBody(exchange);
            MediaRequest request = JsonUtil.fromJson(requestBody, MediaRequest.class);

            at.fhtw.swen1.mrp.entity.Media media = mediaService.updateMedia(
                    Long.parseLong(mediaId),
                    request.getTitle(),
                    request.getDescription(),
                    request.getMediaType(),
                    request.getReleaseYear(),
                    request.getGenres(),
                    request.getAgeRestriction(),
                    userId
            );

            MediaResponse response = new MediaResponse(
                    media.getId(),
                    media.getTitle(),
                    media.getDescription(),
                    media.getMediaType(),
                    media.getReleaseYear(),
                    media.getGenres(),
                    media.getAgeRestriction(),
                    media.getCreatorId(),
                    media.getCreatedAt()
            );

            sendResponse(exchange, 200, JsonUtil.toJson(response));
        } catch (SecurityException e) {
            sendResponse(exchange, 403, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 404, "{\"error\":\"Media not found\"}");
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\":\"Update failed: " + e.getMessage() + "\"}");
        }
    }

    private void handleDeleteMedia(HttpExchange exchange, String mediaId) throws IOException {
        try {
            Long userId = getUserIdFromToken(exchange);
            mediaService.deleteMedia(Long.parseLong(mediaId), userId);
            sendResponse(exchange, 204, "");
        } catch (SecurityException e) {
            sendResponse(exchange, 403, "{\"error\":\"" + e.getMessage() + "\"}");
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

    private Long getUserIdFromToken(HttpExchange exchange) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        String username = authService.getUsernameFromToken(token);
        if (username == null) {
            throw new IllegalStateException("Ungültiger Token");
        }
        at.fhtw.swen1.mrp.entity.User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new IllegalStateException("User nicht gefunden");
        }
        return user.getId();
    }
}

