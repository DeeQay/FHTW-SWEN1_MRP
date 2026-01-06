package at.fhtw.swen1.mrp.dao;

import at.fhtw.swen1.mrp.entity.Favorite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO für Favorite Database Operations
 */
public class FavoriteDAO {

    // Favorit speichern
    public void save(Connection conn, Favorite favorite) {
        String sql = "INSERT INTO favorites (user_id, media_id, created_at) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, favorite.getUserId());
            stmt.setLong(2, favorite.getMediaId());
            stmt.setTimestamp(3, Timestamp.valueOf(favorite.getCreatedAt()));

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                favorite.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Speichern des Favoriten: " + e.getMessage(), e);
        }
    }

    // Favorit löschen
    public void deleteByUserAndMedia(Connection conn, Long userId, Long mediaId) {
        String sql = "DELETE FROM favorites WHERE user_id = ? AND media_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setLong(2, mediaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Löschen des Favoriten: " + e.getMessage(), e);
        }
    }

    // Alle Favoriten eines Users
    public List<Favorite> findByUserId(Connection conn, Long userId) {
        String sql = "SELECT * FROM favorites WHERE user_id = ? ORDER BY created_at DESC";
        List<Favorite> favorites = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                favorites.add(mapResultSetToFavorite(rs));
            }
            return favorites;
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden der Favoriten: " + e.getMessage(), e);
        }
    }

    // Prüfen ob User Media bereits als Favorit markiert hat
    public boolean existsByUserAndMedia(Connection conn, Long userId, Long mediaId) {
        String sql = "SELECT 1 FROM favorites WHERE user_id = ? AND media_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setLong(2, mediaId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler bei existsByUserAndMedia: " + e.getMessage(), e);
        }
    }

    // ResultSet zu Favorite Mapping
    private Favorite mapResultSetToFavorite(ResultSet rs) throws SQLException {
        Favorite favorite = new Favorite();
        favorite.setId(rs.getLong("id"));
        favorite.setUserId(rs.getLong("user_id"));
        favorite.setMediaId(rs.getLong("media_id"));

        Timestamp timestamp = rs.getTimestamp("created_at");
        if (timestamp != null) {
            favorite.setCreatedAt(timestamp.toLocalDateTime());
        }

        return favorite;
    }
}

