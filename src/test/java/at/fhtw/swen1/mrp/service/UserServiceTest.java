package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.dao.MediaDAO;
import at.fhtw.swen1.mrp.dao.RatingDAO;
import at.fhtw.swen1.mrp.dao.UserDAO;
import at.fhtw.swen1.mrp.dto.response.LeaderboardEntryResponse;
import at.fhtw.swen1.mrp.dto.response.UserStatisticsResponse;
import at.fhtw.swen1.mrp.entity.Media;
import at.fhtw.swen1.mrp.entity.Rating;
import at.fhtw.swen1.mrp.entity.User;
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
 * Unit Tests für UserService
 */
class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private RatingDAO ratingDAO;

    @Mock
    private MediaDAO mediaDAO;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userDAO, ratingDAO, mediaDAO);
    }

    @Test
    void testGetUserStatistics_WithRatings_ShouldReturnCorrectStatistics() {
        // Arrange: User mit 3 Ratings
        Rating r1 = new Rating(1L, 100L, 3, "OK");
        Rating r2 = new Rating(1L, 101L, 4, "Gut");
        Rating r3 = new Rating(1L, 102L, 5, "Super");
        List<Rating> ratings = Arrays.asList(r1, r2, r3);

        Media m1 = new Media();
        m1.setGenres(Arrays.asList("Action", "Thriller"));
        Media m2 = new Media();
        m2.setGenres(Arrays.asList("Action", "Drama"));
        Media m3 = new Media();
        m3.setGenres(Arrays.asList("Comedy"));

        when(ratingDAO.findByUserId(any(Connection.class), eq(1L))).thenReturn(ratings);
        when(mediaDAO.findById(any(Connection.class), eq(100L))).thenReturn(m1);
        when(mediaDAO.findById(any(Connection.class), eq(101L))).thenReturn(m2);
        when(mediaDAO.findById(any(Connection.class), eq(102L))).thenReturn(m3);

        // Act
        UserStatisticsResponse stats = userService.getUserStatistics(1L);

        // Assert
        assertNotNull(stats);
        assertEquals(3, stats.getTotalRatings());
        assertEquals(4.0, stats.getAverageScore(), 0.001);
        assertEquals("Action", stats.getFavoriteGenre()); // 2x Action
    }

    @Test
    void testGetUserStatistics_NoRatings_ShouldReturnEmptyStatistics() {
        // Arrange: User ohne Ratings
        when(ratingDAO.findByUserId(any(Connection.class), eq(1L))).thenReturn(Collections.emptyList());

        // Act
        UserStatisticsResponse stats = userService.getUserStatistics(1L);

        // Assert
        assertNotNull(stats);
        assertEquals(0, stats.getTotalRatings());
        assertNull(stats.getAverageScore());
        assertNull(stats.getFavoriteGenre());
    }

    @Test
    void testGetLeaderboard_ShouldReturnTopUsers() {
        // Arrange: Leaderboard mit 3 Einträgen
        List<LeaderboardEntryResponse> leaderboard = Arrays.asList(
                new LeaderboardEntryResponse(1, "alice", 10),
                new LeaderboardEntryResponse(2, "bob", 8),
                new LeaderboardEntryResponse(3, "charlie", 5)
        );

        when(userDAO.findLeaderboard(any(Connection.class), eq(10))).thenReturn(leaderboard);

        // Act
        List<LeaderboardEntryResponse> result = userService.getLeaderboard(10);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());

        assertEquals(1, result.get(0).getRank());
        assertEquals("alice", result.get(0).getUsername());
        assertEquals(10, result.get(0).getRatingCount());

        assertEquals(2, result.get(1).getRank());
        assertEquals("bob", result.get(1).getUsername());
        assertEquals(8, result.get(1).getRatingCount());

        assertEquals(3, result.get(2).getRank());
        assertEquals("charlie", result.get(2).getUsername());
        assertEquals(5, result.get(2).getRatingCount());
    }

    @Test
    void testGetUserRatings_ShouldReturnUserRatingHistory() {
        // Arrange: User mit 2 Ratings
        Rating r1 = new Rating(1L, 100L, 5, "Great");
        Rating r2 = new Rating(1L, 101L, 3, "OK");
        List<Rating> ratings = Arrays.asList(r1, r2);

        when(ratingDAO.findByUserId(any(Connection.class), eq(1L))).thenReturn(ratings);

        // Act
        List<Rating> result = userService.getUserRatings(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(5, result.get(0).getScore());
        assertEquals(3, result.get(1).getScore());
    }
}
