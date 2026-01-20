package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.dao.RatingDAO;
import at.fhtw.swen1.mrp.dao.RatingLikeDAO;
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
 * Unit Tests für RatingService
 */
class RatingServiceTest {

    @Mock
    private RatingDAO ratingDAO;

    @Mock
    private RatingLikeDAO ratingLikeDAO;

    private RatingService ratingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ratingService = new RatingService(ratingDAO, ratingLikeDAO);
    }

    @Test
    void testCalculateAverageRating_WithRatings_ShouldReturnCorrectAverage() {
        // Arrange: 3 Ratings mit Scores 3, 4, 5 -> Durchschnitt 4.0
        Rating r1 = new Rating(1L, 100L, 3, "OK");
        Rating r2 = new Rating(2L, 100L, 4, "Gut");
        Rating r3 = new Rating(3L, 100L, 5, "Super");
        List<Rating> ratings = Arrays.asList(r1, r2, r3);

        when(ratingDAO.findByMediaId(any(Connection.class), eq(100L))).thenReturn(ratings);

        // Act
        Double average = ratingService.calculateAverageRating(100L);

        // Assert
        assertNotNull(average);
        assertEquals(4.0, average, 0.001);
    }

    @Test
    void testCalculateAverageRating_NoRatings_ShouldReturnNull() {
        // Arrange: keine Ratings vorhanden
        when(ratingDAO.findByMediaId(any(Connection.class), eq(100L))).thenReturn(Collections.emptyList());

        // Act
        Double average = ratingService.calculateAverageRating(100L);

        // Assert
        assertNull(average);
    }

    @Test
    void testGetRatingById_ExistingRating_ShouldReturnRating() {
        // Arrange
        Rating expected = new Rating(1L, 100L, 5, "Test");
        expected.setId(42L);
        when(ratingDAO.findById(any(Connection.class), eq(42L))).thenReturn(expected);

        // Act
        Rating result = ratingService.getRatingById(42L);

        // Assert
        assertNotNull(result);
        assertEquals(42L, result.getId());
        assertEquals(5, result.getScore());
        assertEquals("Test", result.getComment());
    }

    @Test
    void testGetRatingsByMediaId_ShouldReturnAllRatings() {
        // Arrange
        Rating r1 = new Rating(1L, 100L, 4, "Comment1");
        Rating r2 = new Rating(2L, 100L, 3, "Comment2");
        when(ratingDAO.findByMediaId(any(Connection.class), eq(100L))).thenReturn(Arrays.asList(r1, r2));

        // Act
        List<Rating> ratings = ratingService.getRatingsByMediaId(100L);

        // Assert
        assertNotNull(ratings);
        assertEquals(2, ratings.size());
        assertEquals(4, ratings.get(0).getScore());
        assertEquals(3, ratings.get(1).getScore());
    }
}
