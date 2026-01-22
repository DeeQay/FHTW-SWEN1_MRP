package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.dao.MediaDAO;
import at.fhtw.swen1.mrp.dao.RatingDAO;
import at.fhtw.swen1.mrp.entity.Media;
import at.fhtw.swen1.mrp.entity.Rating;
import at.fhtw.swen1.mrp.util.DatabaseConnection;

import java.util.*;
import java.util.stream.Collectors;

public class RecommendationService {

    private final RatingDAO ratingDAO;
    private final MediaDAO mediaDAO;

    public RecommendationService() {
        this.ratingDAO = new RatingDAO();
        this.mediaDAO = new MediaDAO();
    }

    // Constructor Injection für Testbarkeit
    public RecommendationService(RatingDAO ratingDAO, MediaDAO mediaDAO) {
        this.ratingDAO = ratingDAO;
        this.mediaDAO = mediaDAO;
    }

    // Genre-basierte Recommendations
    public List<Media> getRecommendationsByGenre(Long userId, int limit) {
        return DatabaseConnection.executeInTransaction(conn -> {
            // User Ratings laden
            List<Rating> userRatings = ratingDAO.findByUserId(conn, userId);

            // keine Ratings
            if (userRatings.isEmpty()) {
                return Collections.emptyList();
            }

            // Bewertete Media-IDs sammeln (zum Ausfiltern)
            Set<Long> ratedMediaIds = userRatings.stream()
                    .map(Rating::getMediaId)
                    .collect(Collectors.toSet());

            // Genres aus bewerteten Media analysieren
            Map<String, Integer> genreCount = new HashMap<>();
            for (Rating rating : userRatings) {
                Media media = mediaDAO.findById(conn, rating.getMediaId());
                if (media != null && media.getGenres() != null) {
                    for (String genre : media.getGenres()) {
                        genreCount.merge(genre, 1, Integer::sum);
                    }
                }
            }

            // Top 3 Genres ermitteln
            List<String> topGenres = genreCount.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(3)
                    .map(Map.Entry::getKey)
                    .toList();

            // keine Genres
            if (topGenres.isEmpty()) {
                return Collections.emptyList();
            }

            // Alle Media laden
            List<Media> allMedia = mediaDAO.findAll(conn);

            // Filtern: Media mit Top-Genres, die noch nicht bewertet wurden
            List<Media> recommendations = allMedia.stream()
                    .filter(m -> !ratedMediaIds.contains(m.getId()))
                    .filter(m -> m.getGenres() != null &&
                            m.getGenres().stream().anyMatch(topGenres::contains))
                    .limit(limit)
                    .toList();

            return recommendations;
        });
    }
}
