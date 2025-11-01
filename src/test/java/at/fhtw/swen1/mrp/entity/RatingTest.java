package at.fhtw.swen1.mrp.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests für Rating Entity
 * Testet Rating-Objekt Funktionalität
 */
class RatingTest {

    @Test
    void testRatingConstructor_WithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        Rating rating = new Rating(1L, 1L, 1L, 5, "Great movie!", now, now);

        assertEquals(1L, rating.getId());
        assertEquals(1L, rating.getUserId());
        assertEquals(1L, rating.getMediaId());
        assertEquals(5, rating.getScore());
        assertEquals("Great movie!", rating.getComment());
    }

    @Test
    void testRatingConstructor_WithoutId() {
        Rating rating = new Rating(1L, 1L, 5, "Great movie!");

        assertNull(rating.getId());
        assertEquals(5, rating.getScore());
        assertNotNull(rating.getCreatedAt());
    }

    @Test
    void testRatingSettersAndGetters() {
        Rating rating = new Rating();
        rating.setUserId(1L);
        rating.setMediaId(1L);
        rating.setScore(4);
        rating.setComment("Good movie");

        assertEquals(1L, rating.getUserId());
        assertEquals(4, rating.getScore());
        assertEquals("Good movie", rating.getComment());
    }
}

