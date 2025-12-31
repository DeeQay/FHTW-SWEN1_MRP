package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.dao.RatingDAO;
import at.fhtw.swen1.mrp.entity.Rating;
import at.fhtw.swen1.mrp.util.DatabaseConnection;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service für Rating Business Logic
 */
public class RatingService {

    private final RatingDAO ratingDAO;

    public RatingService() {
        this.ratingDAO = new RatingDAO();
    }

    // Rating erstellen (1 pro User pro Media)
    public Rating createRating(Long userId, Long mediaId, Integer score, String comment) {
        return DatabaseConnection.executeInTransaction(conn -> {
            // Prüfen ob User bereits bewertet hat
            if (ratingDAO.existsByUserAndMedia(conn, userId, mediaId)) {
                throw new IllegalStateException("User hat dieses Media bereits bewertet");
            }

            Rating rating = new Rating(userId, mediaId, score, comment);
            ratingDAO.save(conn, rating);
            return rating;
        });
    }

    // Rating bearbeiten (nur eigene)
    public Rating updateRating(Long ratingId, Long userId, Integer score, String comment) {
        return DatabaseConnection.executeInTransaction(conn -> {
            Rating rating = ratingDAO.findById(conn, ratingId);
            if (rating == null) {
                throw new IllegalArgumentException("Rating nicht gefunden");
            }

            // Nur eigene Ratings bearbeitbar
            if (!rating.getUserId().equals(userId)) {
                throw new SecurityException("Nicht berechtigt dieses Rating zu bearbeiten");
            }

            rating.setScore(score);
            rating.setComment(comment);
            rating.setUpdatedAt(LocalDateTime.now());
            ratingDAO.update(conn, rating);
            return rating;
        });
    }

    // Rating löschen (nur eigene)
    public void deleteRating(Long ratingId, Long userId) {
        DatabaseConnection.executeInTransactionVoid(conn -> {
            Rating rating = ratingDAO.findById(conn, ratingId);
            if (rating == null) {
                throw new IllegalArgumentException("Rating nicht gefunden");
            }

            // Nur eigene Ratings löschbar
            if (!rating.getUserId().equals(userId)) {
                throw new SecurityException("Nicht berechtigt dieses Rating zu löschen");
            }

            ratingDAO.delete(conn, ratingId);
        });
    }

    // Kommentar bestätigen (Moderation)
    public Rating confirmComment(Long ratingId) {
        return DatabaseConnection.executeInTransaction(conn -> {
            Rating rating = ratingDAO.findById(conn, ratingId);
            if (rating == null) {
                throw new IllegalArgumentException("Rating nicht gefunden");
            }

            rating.setIsConfirmed(true);
            rating.setUpdatedAt(LocalDateTime.now());
            ratingDAO.update(conn, rating);
            return rating;
        });
    }

    // Durchschnitt berechnen für eine Media
    public Double calculateAverageRating(Long mediaId) {
        return DatabaseConnection.executeInTransaction(conn -> {
            List<Rating> ratings = ratingDAO.findByMediaId(conn, mediaId);
            if (ratings.isEmpty()) {
                return null;
            }
            return ratings.stream()
                    .mapToInt(Rating::getScore)
                    .average()
                    .getAsDouble();
        });
    }

    // Rating nach ID
    public Rating getRatingById(Long id) {
        return DatabaseConnection.executeInTransaction(conn -> ratingDAO.findById(conn, id));
    }

    // Alle Ratings einer Media
    public List<Rating> getRatingsByMediaId(Long mediaId) {
        return DatabaseConnection.executeInTransaction(conn -> ratingDAO.findByMediaId(conn, mediaId));
    }

    // Rating History eines Users
    public List<Rating> getRatingsByUserId(Long userId) {
        return DatabaseConnection.executeInTransaction(conn -> ratingDAO.findByUserId(conn, userId));
    }
}

