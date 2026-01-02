package at.fhtw.swen1.mrp.dao;

import at.fhtw.swen1.mrp.entity.RatingLike;

import java.sql.*;

/**
 * DAO für RatingLike Database Operations
 */
public class RatingLikeDAO {

    // Like speichern
    public void save(Connection conn, RatingLike like) {
        String sql = "INSERT INTO rating_likes (user_id, rating_id, created_at) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, like.getUserId());
            stmt.setLong(2, like.getRatingId());
            stmt.setTimestamp(3, Timestamp.valueOf(like.getCreatedAt()));

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                like.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Speichern des Like: " + e.getMessage(), e);
        }
    }

    // Prüfen ob User bereits Rating geliked hat
    public boolean existsByUserAndRating(Connection conn, Long userId, Long ratingId) {
        String sql = "SELECT 1 FROM rating_likes WHERE user_id = ? AND rating_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setLong(2, ratingId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler bei existsByUserAndRating: " + e.getMessage(), e);
        }
    }

    // Like löschen (für Unlike)
    public void deleteByUserAndRating(Connection conn, Long userId, Long ratingId) {
        String sql = "DELETE FROM rating_likes WHERE user_id = ? AND rating_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setLong(2, ratingId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Löschen des Like: " + e.getMessage(), e);
        }
    }

    // Anzahl Likes für ein Rating zählen
    public int countByRatingId(Connection conn, Long ratingId) {
        String sql = "SELECT COUNT(*) FROM rating_likes WHERE rating_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, ratingId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Zählen der Likes: " + e.getMessage(), e);
        }
    }
}

