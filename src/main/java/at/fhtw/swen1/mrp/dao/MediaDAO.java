package at.fhtw.swen1.mrp.dao;

import at.fhtw.swen1.mrp.entity.Media;
import at.fhtw.swen1.mrp.util.DatabaseConnection;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MediaDAO {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public void save(Media media) {
        String sql = "INSERT INTO media (title, description, media_type, release_year, genres, age_restriction, created_at) VALUES (?, ?, ?, ?, ?::jsonb, ?, ?)";

        // TODO getconnection im service
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, media.getTitle());
            stmt.setString(2, media.getDescription());
            stmt.setString(3, media.getMediaType());
            stmt.setObject(4, media.getReleaseYear());
            stmt.setString(5, objectMapper.writeValueAsString(media.getGenres()));
            stmt.setString(6, media.getAgeRestriction());
            stmt.setTimestamp(7, Timestamp.valueOf(media.getCreatedAt()));

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                media.setId(rs.getLong(1));
            }
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Speichern des Media: " + e.getMessage(), e);
        }
    }

    public Media findById(Long id) {
        String sql = "SELECT * FROM media WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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

    public List<Media> findAll() {
        String sql = "SELECT * FROM media ORDER BY id";
        List<Media> mediaList = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                mediaList.add(mapResultSetToMedia(rs));
            }
            return mediaList;
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden aller Media: " + e.getMessage(), e);
        }
    }

    public void update(Media media) {
        String sql = "UPDATE media SET title = ?, description = ?, media_type = ?, release_year = ?, genres = ?::jsonb, age_restriction = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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

    public void delete(Long id) {
        String sql = "DELETE FROM media WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Löschen des Media: " + e.getMessage(), e);
        }
    }

    private Media mapResultSetToMedia(ResultSet rs) throws SQLException {
        Media media = new Media();
        media.setId(rs.getLong("id"));
        media.setTitle(rs.getString("title"));
        media.setDescription(rs.getString("description"));
        media.setMediaType(rs.getString("media_type"));

        Integer releaseYear = (Integer) rs.getObject("release_year");
        media.setReleaseYear(releaseYear);

        String genresJson = rs.getString("genres");
        if (genresJson != null) {
            try {
                List<String> genres = objectMapper.readValue(genresJson, new TypeReference<>() {
                });
                media.setGenres(genres);
            } catch (Exception e) {
                media.setGenres(new ArrayList<>());
            }
        }

        media.setAgeRestriction(rs.getString("age_restriction"));

        Timestamp timestamp = rs.getTimestamp("created_at");
        if (timestamp != null) {
            media.setCreatedAt(timestamp.toLocalDateTime());
        }

        return media;
    }
}
