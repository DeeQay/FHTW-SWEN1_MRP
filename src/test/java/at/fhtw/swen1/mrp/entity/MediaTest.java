package at.fhtw.swen1.mrp.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests für Media Entity
 * Testet Media-Objekt Funktionalität
 */
class MediaTest {

    @Test
    void testMediaConstructor_WithAllFields() {
        List<String> genres = Arrays.asList("Action", "Comedy");
        LocalDateTime now = LocalDateTime.now();

        Media media = new Media(1L, "Test Movie", "Description", "movie", 2024, genres, "PG-13", now);

        assertEquals(1L, media.getId());
        assertEquals("Test Movie", media.getTitle());
        assertEquals("movie", media.getMediaType());
        assertEquals(2024, media.getReleaseYear());
        assertEquals(genres, media.getGenres());
    }

    @Test
    void testMediaConstructor_WithoutId() {
        Media media = new Media("Test Movie", "Description", "movie", 2024);

        assertNull(media.getId());
        assertEquals("Test Movie", media.getTitle());
        assertNotNull(media.getCreatedAt());
    }

    @Test
    void testMediaWithGenres() {
        Media media = new Media();
        List<String> genres = Arrays.asList("Action", "Comedy", "Drama");
        media.setGenres(genres);

        assertEquals(3, media.getGenres().size());
        assertTrue(media.getGenres().contains("Action"));
    }
}

