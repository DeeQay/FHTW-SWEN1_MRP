package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.dao.MediaDAO;
import at.fhtw.swen1.mrp.entity.Media;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit Tests für MediaService
 */
class MediaServiceTest {

    @Mock
    private MediaDAO mediaDAO;

    private MediaService mediaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mediaService = new MediaService(mediaDAO);
    }

    @Test
    void getMediaByIdTest_ExistingMedia_ShouldReturnMedia() {
        // Arrange
        Media expected = new Media();
        expected.setId(1L);
        expected.setTitle("Test Movie");
        expected.setMediaType("movie");
        when(mediaDAO.findById(any(Connection.class), eq(1L))).thenReturn(expected);

        // Act
        Media result = mediaService.getMediaById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Movie", result.getTitle());
    }

    @Test
    void getMediaByIdTest_NonExistingMedia_ShouldThrowException() {
        // Arrange
        when(mediaDAO.findById(any(Connection.class), eq(999L))).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            mediaService.getMediaById(999L);
        });
    }

    @Test
    void searchMediaTest_WithFilters_ShouldReturnFilteredResults() {
        // Arrange
        Media m1 = new Media();
        m1.setId(1L);
        m1.setTitle("Action Movie");
        m1.setGenres(Arrays.asList("Action"));

        Media m2 = new Media();
        m2.setId(2L);
        m2.setTitle("Action Drama");
        m2.setGenres(Arrays.asList("Action", "Drama"));

        when(mediaDAO.findWithFilters(any(Connection.class), eq("Action"), eq(null), eq(null),
                eq(null), eq(null), eq(null), eq(null)))
                .thenReturn(Arrays.asList(m1, m2));

        // Act
        var results = mediaService.searchMedia("Action", null, null, null, null, null, null);

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
    }
}
