package at.fhtw.swen1.mrp.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests für AuthService
 * Testet Token-Generierung und -Validierung
 */
class AuthServiceTest {

    @Test
    void testGenerateToken_ShouldReturnNonNullToken() {
        AuthService authService = new AuthService();
        String token = authService.generateToken("testuser");

        assertNotNull(token);
        assertTrue(token.contains("testuser"));
        assertTrue(token.contains("-token-"));
    }

    @Test
    void testValidateToken_ValidToken_ShouldReturnTrue() {
        AuthService authService = new AuthService();
        String token = authService.generateToken("testuser");

        assertTrue(authService.validateToken(token));
    }

    @Test
    void testValidateToken_InvalidToken_ShouldReturnFalse() {
        AuthService authService = new AuthService();

        assertFalse(authService.validateToken("invalid-token"));
        assertFalse(authService.validateToken(null));
    }

    @Test
    void testInvalidateToken_ShouldRemoveToken() {
        AuthService authService = new AuthService();
        String token = authService.generateToken("testuser");

        assertTrue(authService.validateToken(token));
        authService.invalidateToken(token);
        assertFalse(authService.validateToken(token));
    }
}

