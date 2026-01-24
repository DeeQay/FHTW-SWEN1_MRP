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

    // Constructor zum Testen
    public RecommendationService(RatingDAO ratingDAO, MediaDAO mediaDAO) {
        this.ratingDAO = ratingDAO;
        this.mediaDAO = mediaDAO;
    }

    // Genre-basierte Recommendations
    public List<Media> getRecommendationsByGenre(Long userId, int limit) {
        return DatabaseConnection.executeInTransaction(conn -> {
            List<Rating> userRatings = ratingDAO.findByUserId(conn, userId);

            if (userRatings.isEmpty()) {
                return Collections.emptyList();
            }

            // Bewertete Media-IDs sammeln (zum Ausfiltern)
            Set<Long> ratedMediaIds = userRatings.stream()
                    .map(Rating::getMediaId)
                    .collect(Collectors.toSet());

            // Genres aus allen Ratings analysieren, gewichtet nach Score
            Map<String, Integer> genreScore = new HashMap<>();
            for (Rating rating : userRatings) {
                Media media = mediaDAO.findById(conn, rating.getMediaId());
                if (media != null && media.getGenres() != null) {
                    for (String genre : media.getGenres()) {
                        genreScore.merge(genre, rating.getScore(), Integer::sum);
                    }
                }
            }

            // Top 3 Genres nach Score-Summe ermitteln
            List<String> topGenres = genreScore.entrySet().stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .limit(3)
                    .map(Map.Entry::getKey)
                    .toList();

            if (topGenres.isEmpty()) {
                return Collections.emptyList();
            }

            // Alle Media laden
            List<Media> allMedia = mediaDAO.findAll(conn);

            // Filtern: Media mit Top-Genres, die noch nicht bewertet wurden
            return allMedia.stream()
                    .filter(m -> !ratedMediaIds.contains(m.getId()))
                    .filter(m -> m.getGenres() != null &&
                            m.getGenres().stream().anyMatch(topGenres::contains))
                    .limit(limit)
                    .toList();
        });
    }

    // Content Similarity: Matching von Genres, mediaType, ageRestriction
    public List<Media> getRecommendationsByContent(Long userId, int limit) {
        return DatabaseConnection.executeInTransaction(conn -> {
            List<Rating> userRatings = ratingDAO.findByUserId(conn, userId);

            if (userRatings.isEmpty()) {
                return Collections.emptyList();
            }

            // Bewertete Media-IDs (zum Ausfiltern)
            Set<Long> ratedMediaIds = userRatings.stream()
                    .map(Rating::getMediaId)
                    .collect(Collectors.toSet());

            // Nur positiv bewertete Media analysieren (Score >= 3/5)
            List<Rating> positiveRatings = userRatings.stream()
                    .filter(r -> r.getScore() >= 3)
                    .toList();

            if (positiveRatings.isEmpty()) {
                return Collections.emptyList();
            }

            // Präferenzen aus positiv bewerteten Media sammeln
            Set<String> preferredGenres = new HashSet<>();
            Set<String> preferredMediaTypes = new HashSet<>();
            Set<String> preferredAgeRestrictions = new HashSet<>();

            for (Rating rating : positiveRatings) {
                Media media = mediaDAO.findById(conn, rating.getMediaId());
                if (media == null) continue;

                if (media.getGenres() != null) {
                    preferredGenres.addAll(media.getGenres());
                }
                if (media.getMediaType() != null) {
                    preferredMediaTypes.add(media.getMediaType());
                }
                if (media.getAgeRestriction() != null) {
                    preferredAgeRestrictions.add(media.getAgeRestriction());
                }
            }

            // Alle Media laden
            List<Media> allMedia = mediaDAO.findAll(conn);

            // Similarity Score berechnen und sortieren
            List<Media> candidates = new ArrayList<>();
            for (Media media : allMedia) {
                if (!ratedMediaIds.contains(media.getId())) {
                    int score = calculateSimilarityScore(media, preferredGenres, preferredMediaTypes, preferredAgeRestrictions);
                    if (score > 0) {
                        candidates.add(media);
                    }
                }
            }

            // Nach Score sortieren (höchster zuerst)
            candidates.sort((m1, m2) -> {
                int score1 = calculateSimilarityScore(m1, preferredGenres, preferredMediaTypes, preferredAgeRestrictions);
                int score2 = calculateSimilarityScore(m2, preferredGenres, preferredMediaTypes, preferredAgeRestrictions);
                return Integer.compare(score2, score1);
            });

            // Limit anwenden
            if (candidates.size() > limit) {
                return candidates.subList(0, limit);
            }
            return candidates;
        });
    }

    // Similarity Score: Anzahl Übereinstimmungen zählen
    private int calculateSimilarityScore(Media media, Set<String> preferredGenres,
                                         Set<String> preferredMediaTypes,
                                         Set<String> preferredAgeRestrictions) {
        int score = 0;

        // Genre Matching
        if (media.getGenres() != null) {
            for (String genre : media.getGenres()) {
                if (preferredGenres.contains(genre)) {
                    score++;
                }
            }
        }

        // MediaType Matching
        if (media.getMediaType() != null && preferredMediaTypes.contains(media.getMediaType())) {
            score++;
        }

        // AgeRestriction Matching
        if (media.getAgeRestriction() != null && preferredAgeRestrictions.contains(media.getAgeRestriction())) {
            score++;
        }

        return score;
    }
}
