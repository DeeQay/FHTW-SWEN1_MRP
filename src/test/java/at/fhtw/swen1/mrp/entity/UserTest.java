package at.fhtw.swen1.mrp.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests für User Entity
 * Testet User-Objekt Funktionalität
 */
class UserTest {

    @Test
    void testUserConstructor_WithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User(1L, "testuser", "hashedpassword", "test@example.com", now);

        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("hashedpassword", user.getPasswordHash());
        assertEquals("test@example.com", user.getEmail());
        assertEquals(now, user.getCreatedAt());
    }

    @Test
    void testUserConstructor_WithoutId() {
        User user = new User("testuser", "hashedpassword", "test@example.com");

        assertNull(user.getId());
        assertEquals("testuser", user.getUsername());
        assertNotNull(user.getCreatedAt());
    }

    @Test
    void testUserSettersAndGetters() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPasswordHash("hashedpassword");
        user.setEmail("test@example.com");

        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("hashedpassword", user.getPasswordHash());
        assertEquals("test@example.com", user.getEmail());
    }
}

