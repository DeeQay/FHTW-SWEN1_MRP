package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.dao.FavoriteDAO;
import at.fhtw.swen1.mrp.dao.MediaDAO;
import at.fhtw.swen1.mrp.entity.Favorite;
import at.fhtw.swen1.mrp.entity.Media;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit Tests für FavoriteService
 */
class FavoriteServiceTest {

    @Mock
    private FavoriteDAO favoriteDAO;

    @Mock
    private MediaDAO mediaDAO;

    private FavoriteService favoriteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        favoriteService = new FavoriteService(favoriteDAO, mediaDAO);
    }

    @Test
    void addFavoriteTest_ValidMedia_ShouldAddFavorite() {
        // Arrange
        Media media = new Media();
        media.setId(100L);
        media.setTitle("Test Movie");

        when(mediaDAO.findById(any(Connection.class), eq(100L))).thenReturn(media);
        when(favoriteDAO.existsByUserAndMedia(any(Connection.class), eq(1L), eq(100L))).thenReturn(false);

        // Act
        Favorite result = favoriteService.addFavorite(1L, 100L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(100L, result.getMediaId());
    }

    @Test
    void addFavoriteTest_MediaNotFound_ShouldThrowException() {
        // Arrange
        when(mediaDAO.findById(any(Connection.class), eq(999L))).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            favoriteService.addFavorite(1L, 999L);
        });
    }

    @Test
    void addFavoriteTest_AlreadyFavorite_ShouldThrowException() {
        // Arrange
        Media media = new Media();
        media.setId(100L);

        when(mediaDAO.findById(any(Connection.class), eq(100L))).thenReturn(media);
        when(favoriteDAO.existsByUserAndMedia(any(Connection.class), eq(1L), eq(100L))).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            favoriteService.addFavorite(1L, 100L);
        });
    }

    @Test
    void getFavoritesByUserIdTest_ShouldReturnFavorites() {
        // Arrange
        Favorite f1 = new Favorite(1L, 100L);
        Favorite f2 = new Favorite(1L, 101L);
        when(favoriteDAO.findByUserId(any(Connection.class), eq(1L))).thenReturn(Arrays.asList(f1, f2));

        // Act
        List<Favorite> favorites = favoriteService.getFavoritesByUserId(1L);

        // Assert
        assertNotNull(favorites);
        assertEquals(2, favorites.size());
    }
}
