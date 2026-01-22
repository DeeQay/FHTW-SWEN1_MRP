package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.dao.MediaDAO;
import at.fhtw.swen1.mrp.dao.RatingDAO;
import at.fhtw.swen1.mrp.dao.UserDAO;
import at.fhtw.swen1.mrp.dto.response.LeaderboardEntryResponse;
import at.fhtw.swen1.mrp.dto.response.UserStatisticsResponse;
import at.fhtw.swen1.mrp.entity.Media;
import at.fhtw.swen1.mrp.entity.Rating;
import at.fhtw.swen1.mrp.entity.User;
import at.fhtw.swen1.mrp.util.DatabaseConnection;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {
    // OLD: Memory Map statt DAO mit Datenbank
    //private static final Map<String, User> userStore = new ConcurrentHashMap<>();
    //private static long userIdCounter = 1L;

    private final UserDAO userDAO;
    private final RatingDAO ratingDAO;
    private final MediaDAO mediaDAO;

    public UserService() {
        this.userDAO = new UserDAO();
        this.ratingDAO = new RatingDAO();
        this.mediaDAO = new MediaDAO();
    }

    // Constructor für Tests
    public UserService(UserDAO userDAO, RatingDAO ratingDAO, MediaDAO mediaDAO) {
        this.userDAO = userDAO;
        this.ratingDAO = ratingDAO;
        this.mediaDAO = mediaDAO;
    }

    public User registerUser(String username, String password, String email) {
        return DatabaseConnection.executeInTransaction(conn -> {
            // Prüfen ob User bereits existiert
            User existingUser = userDAO.findByUsername(conn, username);
            if (existingUser != null) {
                throw new IllegalArgumentException("Username bereits vorhanden");
            }

            String hashedPassword = hashPassword(password);
            User user = new User();
            user.setUsername(username);
            user.setPasswordHash(hashedPassword);
            user.setEmail(email);

            user.setCreatedAt(LocalDateTime.now());

            userDAO.save(conn, user);
            return user;
            // OLD: Memory Map statt DAO mit Datenbank
            //if (userStore.containsKey(username)) {
            //    throw new IllegalArgumentException("Username bereits vorhanden");
            //}
            //String hashedPassword = hashPassword(password);
            //User user = new User();
            //user.setId(userIdCounter++);
            //user.setUsername(username);
            //user.setPasswordHash(hashedPassword);
            //user.setEmail(email);
            //user.setCreatedAt(LocalDateTime.now());
            //userStore.put(username, user);
            //return user;
        });
    }

    public User loginUser(String username, String password) {
        return DatabaseConnection.executeInTransaction(conn -> {
            User user = userDAO.findByUsername(conn, username);
            if (user == null) {
                throw new IllegalArgumentException("Ungültige Credentials");
            }

            String hashedPassword = hashPassword(password);
            if (!user.getPasswordHash().equals(hashedPassword)) {
                throw new IllegalArgumentException("Ungültige Credentials");
            }

            return user;

            // OLD: Memory Map statt DAO mit Datenbank
            //User user = userStore.get(username);
            //if (user == null) {
            //    throw new IllegalArgumentException("Ungültige Credentials");
            //}
            //String hashedPassword = hashPassword(password);
            //if (!user.getPasswordHash().equals(hashedPassword)) {
            //    throw new IllegalArgumentException("Ungültige Credentials");
            //}
            //return user;
        });
    }

    public User getUserByUsername(String username) {
        return DatabaseConnection.executeInTransaction(conn -> userDAO.findByUsername(conn, username));

        // OLD: Memory Map statt DAO mit Datenbank
        //return userStore.get(username);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 nicht verfügbar", e);
        }
    }

    // User Statistics berechnen (totalRatings, avgScore, favoriteGenre)
    public UserStatisticsResponse getUserStatistics(Long userId) {
        return DatabaseConnection.executeInTransaction(conn -> {
            List<Rating> ratings = ratingDAO.findByUserId(conn, userId);

            // Ratings empty
            if (ratings.isEmpty()) {
                return new UserStatisticsResponse(0, null, null);
            }

            int totalRatings = ratings.size();

            // average Score berechnen
            double avgScore = ratings.stream()
                    .mapToInt(Rating::getScore)
                    .average()
                    .orElse(0.0);

            // Favorite Genre (meistbewertetes Genre)
            Map<String, Integer> genreCount = new HashMap<>();
            for (Rating rating : ratings) {
                Media media = mediaDAO.findById(conn, rating.getMediaId());
                if (media != null && media.getGenres() != null) {
                    for (String genre : media.getGenres()) {
                        genreCount.merge(genre, 1, Integer::sum);
                    }
                }
            }

            // Genre mit höchster Anzahl finden
            String favoriteGenre = genreCount.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);

            return new UserStatisticsResponse(totalRatings, avgScore, favoriteGenre);
        });
    }

    // Rating History eines Users laden
    public List<Rating> getUserRatings(Long userId) {
        return DatabaseConnection.executeInTransaction(conn -> ratingDAO.findByUserId(conn, userId));
    }

    // Leaderboard: Top Users nach Anzahl Ratings
    public List<LeaderboardEntryResponse> getLeaderboard(int limit) {
        return DatabaseConnection.executeInTransaction(conn -> userDAO.findLeaderboard(conn, limit));
    }

    // User Profile (Email) aktualisieren
    public User updateUserProfile(String username, String newEmail) {
        return DatabaseConnection.executeInTransaction(conn -> {
            User user = userDAO.findByUsername(conn, username);
            if (user == null) {
                throw new IllegalArgumentException("User nicht gefunden");
            }

            if (newEmail == null || newEmail.trim().isEmpty()) {
                throw new IllegalArgumentException("Email darf nicht leer sein");
            }

            user.setEmail(newEmail.trim());
            userDAO.update(conn, user);
            return user;
        });
    }
}
