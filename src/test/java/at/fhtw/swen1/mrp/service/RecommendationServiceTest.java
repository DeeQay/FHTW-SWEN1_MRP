package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.dao.MediaDAO;
import at.fhtw.swen1.mrp.dao.RatingDAO;
import at.fhtw.swen1.mrp.entity.Media;
import at.fhtw.swen1.mrp.entity.Rating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit Tests für RecommendationService
 */
class RecommendationServiceTest {

    @Mock
    private RatingDAO ratingDAO;

    @Mock
    private MediaDAO mediaDAO;

    private RecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        recommendationService = new RecommendationService(ratingDAO, mediaDAO);
    }

    @Test
    void testGetRecommendationsByGenre_WithRatings_ShouldReturnMatchingMedia() {
        // Arrange: User hat Action/Drama Filme bewertet
        Rating r1 = new Rating(1L, 100L, 5, "Great");
        Rating r2 = new Rating(1L, 101L, 4, "Good");
        List<Rating> userRatings = Arrays.asList(r1, r2);

        Media ratedMedia1 = new Media();
        ratedMedia1.setId(100L);
        ratedMedia1.setGenres(Arrays.asList("Action", "Thriller"));

        Media ratedMedia2 = new Media();
        ratedMedia2.setId(101L);
        ratedMedia2.setGenres(Arrays.asList("Action", "Drama"));

        // Recommendation Candidates
        Media candidate1 = new Media();
        candidate1.setId(200L);
        candidate1.setGenres(Arrays.asList("Action", "Sci-Fi")); // Match: Action

        Media candidate2 = new Media();
        candidate2.setId(201L);
        candidate2.setGenres(Arrays.asList("Comedy", "Romance")); // No Match

        Media candidate3 = new Media();
        candidate3.setId(202L);
        candidate3.setGenres(Arrays.asList("Drama", "Romance")); // Match: Drama

        List<Media> allMedia = Arrays.asList(ratedMedia1, ratedMedia2, candidate1, candidate2, candidate3);

        when(ratingDAO.findByUserId(any(Connection.class), eq(1L))).thenReturn(userRatings);
        when(mediaDAO.findById(any(Connection.class), eq(100L))).thenReturn(ratedMedia1);
        when(mediaDAO.findById(any(Connection.class), eq(101L))).thenReturn(ratedMedia2);
        when(mediaDAO.findAll(any(Connection.class))).thenReturn(allMedia);

        // Act
        List<Media> recommendations = recommendationService.getRecommendationsByGenre(1L, 10);

        // Assert
        assertNotNull(recommendations);
        assertEquals(2, recommendations.size()); // candidate1 und candidate3
        assertTrue(recommendations.stream().anyMatch(m -> m.getId().equals(200L)));
        assertTrue(recommendations.stream().anyMatch(m -> m.getId().equals(202L)));
        assertFalse(recommendations.stream().anyMatch(m -> m.getId().equals(201L))); // Comedy nicht in Top Genres
        assertFalse(recommendations.stream().anyMatch(m -> m.getId().equals(100L))); // bereits bewertet
    }

    @Test
    void testGetRecommendationsByGenre_NoRatings_ShouldReturnEmpty() {
        // Arrange: User ohne Ratings
        when(ratingDAO.findByUserId(any(Connection.class), eq(1L))).thenReturn(Collections.emptyList());

        // Act
        List<Media> recommendations = recommendationService.getRecommendationsByGenre(1L, 10);

        // Assert
        assertNotNull(recommendations);
        assertTrue(recommendations.isEmpty());
    }

    @Test
    void testGetRecommendationsByGenre_AllMediaRated_ShouldReturnEmpty() {
        // Arrange: User hat alle verfügbaren Media bewertet
        Rating r1 = new Rating(1L, 100L, 5, "Great");
        List<Rating> userRatings = Collections.singletonList(r1);

        Media ratedMedia = new Media();
        ratedMedia.setId(100L);
        ratedMedia.setGenres(Arrays.asList("Action"));

        List<Media> allMedia = Collections.singletonList(ratedMedia);

        when(ratingDAO.findByUserId(any(Connection.class), eq(1L))).thenReturn(userRatings);
        when(mediaDAO.findById(any(Connection.class), eq(100L))).thenReturn(ratedMedia);
        when(mediaDAO.findAll(any(Connection.class))).thenReturn(allMedia);

        // Act
        List<Media> recommendations = recommendationService.getRecommendationsByGenre(1L, 10);

        // Assert
        assertNotNull(recommendations);
        assertTrue(recommendations.isEmpty());
    }
}
