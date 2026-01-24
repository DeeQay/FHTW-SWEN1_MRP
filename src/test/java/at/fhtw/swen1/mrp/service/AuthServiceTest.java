package at.fhtw.swen1.mrp.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests für AuthService
 */
class AuthServiceTest {

    @Test
    void generateTokenTest_ShouldReturnNonNullToken() {
        AuthService authService = new AuthService();
        String token = authService.generateToken("testuser");

        assertNotNull(token);
        assertTrue(token.contains("testuser"));
        assertTrue(token.contains("-token-"));
    }

    @Test
    void validateTokenTest_ValidToken_ShouldReturnTrue() {
        AuthService authService = new AuthService();
        String token = authService.generateToken("testuser");

        assertTrue(authService.validateToken(token));
    }

    @Test
    void validateTokenTest_InvalidToken_ShouldReturnFalse() {
        AuthService authService = new AuthService();

        assertFalse(authService.validateToken("invalid-token"));
        assertFalse(authService.validateToken(null));
    }

    @Test
    void invalidateTokenTest_ShouldRemoveToken() {
        AuthService authService = new AuthService();
        String token = authService.generateToken("testuser");

        assertTrue(authService.validateToken(token));
        authService.invalidateToken(token);
        assertFalse(authService.validateToken(token));
    }

    @Test
    void getUsernameFromTokenTest_ValidToken_ShouldReturnUsername() {
        AuthService authService = new AuthService();
        String token = authService.generateToken("testuser");

        String username = authService.getUsernameFromToken(token);

        assertEquals("testuser", username);
    }

    @Test
    void getUsernameFromTokenTest_InvalidToken_ShouldReturnNull() {
        AuthService authService = new AuthService();

        String username = authService.getUsernameFromToken("invalid-token");

        assertNull(username);
    }
}
