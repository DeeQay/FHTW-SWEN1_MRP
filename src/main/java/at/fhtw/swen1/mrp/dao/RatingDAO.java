package at.fhtw.swen1.mrp.dao;

import at.fhtw.swen1.mrp.entity.Rating;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO für Rating Database Operations
 */
public class RatingDAO {

    // Rating in DB speichern
    public void save(Connection conn, Rating rating) {
        String sql = "INSERT INTO ratings (user_id, media_id, score, comment, is_confirmed, like_count, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, rating.getUserId());
            stmt.setLong(2, rating.getMediaId());
            stmt.setInt(3, rating.getScore());
            stmt.setString(4, rating.getComment());
            stmt.setBoolean(5, rating.getIsConfirmed());
            stmt.setInt(6, rating.getLikeCount());
            stmt.setTimestamp(7, Timestamp.valueOf(rating.getCreatedAt()));
            stmt.setTimestamp(8, Timestamp.valueOf(rating.getUpdatedAt()));

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                rating.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Speichern des Rating: " + e.getMessage(), e);
        }
    }

    public Rating findById(Connection conn, Long id) {
        String sql = "SELECT * FROM ratings WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToRating(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden des Rating: " + e.getMessage(), e);
        }
    }

    // Alle Ratings einer Media laden
    public List<Rating> findByMediaId(Connection conn, Long mediaId) {
        String sql = "SELECT * FROM ratings WHERE media_id = ? ORDER BY created_at DESC";
        List<Rating> ratings = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, mediaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ratings.add(mapResultSetToRating(rs));
            }
            return ratings;
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden der Ratings: " + e.getMessage(), e);
        }
    }

    // Rating History eines Users laden
    public List<Rating> findByUserId(Connection conn, Long userId) {
        String sql = "SELECT * FROM ratings WHERE user_id = ? ORDER BY created_at DESC";
        List<Rating> ratings = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ratings.add(mapResultSetToRating(rs));
            }
            return ratings;
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden der User-Ratings: " + e.getMessage(), e);
        }
    }

    public void update(Connection conn, Rating rating) {
        String sql = "UPDATE ratings SET score = ?, comment = ?, is_confirmed = ?, like_count = ?, updated_at = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rating.getScore());
            stmt.setString(2, rating.getComment());
            stmt.setBoolean(3, rating.getIsConfirmed());
            stmt.setInt(4, rating.getLikeCount());
            stmt.setTimestamp(5, Timestamp.valueOf(rating.getUpdatedAt()));
            stmt.setLong(6, rating.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Aktualisieren des Rating: " + e.getMessage(), e);
        }
    }

    public void delete(Connection conn, Long id) {
        String sql = "DELETE FROM ratings WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Löschen des Rating: " + e.getMessage(), e);
        }
    }

    // Prüfen ob User bereits Rating für Media erstellt hat
    public boolean existsByUserAndMedia(Connection conn, Long userId, Long mediaId) {
        String sql = "SELECT 1 FROM ratings WHERE user_id = ? AND media_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setLong(2, mediaId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler bei existsByUserAndMedia: " + e.getMessage(), e);
        }
    }

    private Rating mapResultSetToRating(ResultSet rs) throws SQLException {
        Rating rating = new Rating();
        rating.setId(rs.getLong("id"));
        rating.setUserId(rs.getLong("user_id"));
        rating.setMediaId(rs.getLong("media_id"));
        rating.setScore(rs.getInt("score"));
        rating.setComment(rs.getString("comment"));
        rating.setIsConfirmed(rs.getBoolean("is_confirmed"));
        rating.setLikeCount(rs.getInt("like_count"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            rating.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            rating.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return rating;
    }
}

