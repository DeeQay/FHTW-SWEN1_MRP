package at.fhtw.swen1.mrp.dao;

import at.fhtw.swen1.mrp.entity.Media;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MediaDAO {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public void save(Connection conn, Media media) {
        String sql = "INSERT INTO media (title, description, media_type, release_year, genres, age_restriction, creator_id, created_at) VALUES (?, ?, ?, ?, ?::jsonb, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, media.getTitle());
            stmt.setString(2, media.getDescription());
            stmt.setString(3, media.getMediaType());
            stmt.setObject(4, media.getReleaseYear());
            stmt.setString(5, objectMapper.writeValueAsString(media.getGenres()));
            stmt.setString(6, media.getAgeRestriction());
            stmt.setObject(7, media.getCreatorId());
            stmt.setTimestamp(8, Timestamp.valueOf(media.getCreatedAt()));

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                media.setId(rs.getLong(1));
            }
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Speichern des Media: " + e.getMessage(), e);
        }
    }

    public Media findById(Connection conn, Long id) {
        String sql = "SELECT * FROM media WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToMedia(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden des Media: " + e.getMessage(), e);
        }
    }

    public List<Media> findAll(Connection conn) {
        String sql = "SELECT * FROM media ORDER BY id";
        List<Media> mediaList = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                mediaList.add(mapResultSetToMedia(rs));
            }
            return mediaList;
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden aller Media: " + e.getMessage(), e);
        }
    }

    public void update(Connection conn, Media media) {
        String sql = "UPDATE media SET title = ?, description = ?, media_type = ?, release_year = ?, genres = ?::jsonb, age_restriction = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, media.getTitle());
            stmt.setString(2, media.getDescription());
            stmt.setString(3, media.getMediaType());
            stmt.setObject(4, media.getReleaseYear());
            stmt.setString(5, objectMapper.writeValueAsString(media.getGenres()));
            stmt.setString(6, media.getAgeRestriction());
            stmt.setLong(7, media.getId());

            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Aktualisieren des Media: " + e.getMessage(), e);
        }
    }

    public void delete(Connection conn, Long id) {
        String sql = "DELETE FROM media WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Löschen des Media: " + e.getMessage(), e);
        }
    }

    // Filter Suche
    public List<Media> findWithFilters(Connection conn, String title, String genre, String mediaType,
                                        Integer releaseYear, String ageRestriction, Double minRating, String sortBy) {
        List<Media> mediaList = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        // Base Query mit optionalem LEFT JOIN für Rating-Berechnung
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT m.*, COALESCE(AVG(r.score), 0) as avg_rating ");
        sql.append("FROM media m ");
        sql.append("LEFT JOIN ratings r ON m.id = r.media_id ");
        sql.append("WHERE 1=1 ");

        // Filter: Title (partial match, case-insensitive)
        if (title != null && !title.isBlank()) {
            sql.append("AND LOWER(m.title) LIKE LOWER(?) ");
            params.add("%" + title + "%");
        }

        // Filter: Genre (prüft ob genre in JSONB Array enthalten ist)
        if (genre != null && !genre.isBlank()) {
            sql.append("AND m.genres @> ?::jsonb ");
            params.add("[\"" + genre + "\"]");
        }

        // Filter: Media Type
        if (mediaType != null && !mediaType.isBlank()) {
            sql.append("AND m.media_type = ? ");
            params.add(mediaType);
        }

        // Filter: Release Year
        if (releaseYear != null) {
            sql.append("AND m.release_year = ? ");
            params.add(releaseYear);
        }

        // Filter: Age Restriction
        if (ageRestriction != null && !ageRestriction.isBlank()) {
            sql.append("AND m.age_restriction = ? ");
            params.add(ageRestriction);
        }

        // GROUP BY für AVG Berechnung
        sql.append("GROUP BY m.id ");

        // Filter: Min Rating (nach GROUP BY als HAVING)
        if (minRating != null && minRating > 0) {
            sql.append("HAVING COALESCE(AVG(r.score), 0) >= ? ");
            params.add(minRating);
        }

        // Sortierung
        sql.append("ORDER BY ");
        if (sortBy != null) {
            switch (sortBy.toLowerCase()) {
                case "title" -> sql.append("m.title ASC");
                case "year" -> sql.append("m.release_year DESC NULLS LAST");
                case "score" -> sql.append("avg_rating DESC");
                default -> sql.append("m.id ASC");
            }
        } else {
            sql.append("m.id ASC");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            // Parameter setzen
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                } else if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof Double) {
                    stmt.setDouble(i + 1, (Double) param);
                }
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                mediaList.add(mapResultSetToMedia(rs));
            }
            return mediaList;
        } catch (SQLException e) {
            throw new RuntimeException("Fehler bei Suche/Filter: " + e.getMessage(), e);
        }
    }

    private Media mapResultSetToMedia(ResultSet rs) throws SQLException {
        Media media = new Media();
        media.setId(rs.getLong("id"));
        media.setTitle(rs.getString("title"));
        media.setDescription(rs.getString("description"));
        media.setMediaType(rs.getString("media_type"));

        Integer releaseYear = rs.getObject("release_year", Integer.class);
        media.setReleaseYear(releaseYear);

        String genresJson = rs.getString("genres");
        if (genresJson != null) {
            try {
                List<String> genres = objectMapper.readValue(genresJson, new TypeReference<>() {});
                media.setGenres(genres);
            } catch (Exception e) {
                media.setGenres(new ArrayList<>());
            }
        }

        media.setAgeRestriction(rs.getString("age_restriction"));

        // Korrigierter Zugriff auf IDs (vermeidet ClassCastException)
        long creatorId = rs.getLong("creator_id");
        if (!rs.wasNull()) {
            media.setCreatorId(creatorId);
        }

        Timestamp timestamp = rs.getTimestamp("created_at");
        if (timestamp != null) {
            media.setCreatedAt(timestamp.toLocalDateTime());
        }

        return media;
    }
}
