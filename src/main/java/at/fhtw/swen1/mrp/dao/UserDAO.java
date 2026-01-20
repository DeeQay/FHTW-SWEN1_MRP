package at.fhtw.swen1.mrp.dao;

import at.fhtw.swen1.mrp.dto.response.LeaderboardEntryResponse;
import at.fhtw.swen1.mrp.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public void save(Connection conn, User user) {
        String sql = "INSERT INTO users (username, password_hash, email, created_at) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getEmail());
            stmt.setTimestamp(4, Timestamp.valueOf(user.getCreatedAt()));

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Speichern des Users: " + e.getMessage(), e);
        }
    }

    public User findByUsername(Connection conn, String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden des Users: " + e.getMessage(), e);
        }
    }

    public User findById(Connection conn, Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden des Users: " + e.getMessage(), e);
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setEmail(rs.getString("email"));

        Timestamp timestamp = rs.getTimestamp("created_at");
        if (timestamp != null) {
            user.setCreatedAt(timestamp.toLocalDateTime());
        }

        return user;
    }

    // Leaderboard: Top 10 Users nach Anzahl Ratings
    public List<LeaderboardEntryResponse> findLeaderboard(Connection conn, int limit) {
        String sql = """
            SELECT u.username, COUNT(r.id) as rating_count
            FROM users u
            LEFT JOIN ratings r ON u.id = r.user_id
            GROUP BY u.id, u.username
            HAVING COUNT(r.id) > 0
            ORDER BY rating_count DESC
            LIMIT ?
            """;
        List<LeaderboardEntryResponse> leaderboard = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LeaderboardEntryResponse entry = new LeaderboardEntryResponse(
                        rs.getString("username"),
                        rs.getInt("rating_count")
                );
                leaderboard.add(entry);
            }
            return leaderboard;
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden des Leaderboards: " + e.getMessage(), e);
        }
    }
}
