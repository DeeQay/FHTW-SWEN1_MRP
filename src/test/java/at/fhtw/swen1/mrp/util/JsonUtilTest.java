package at.fhtw.swen1.mrp.util;

import at.fhtw.swen1.mrp.dto.request.LoginRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests für JsonUtil
 * Testet JSON Serialisierung und Deserialisierung
 * Gesamt: 4 Test-Methoden
 */
class JsonUtilTest {

    @Test
    void testToJson_ValidObject_ShouldReturnJsonString() {
        LoginRequest request = new LoginRequest("testuser", "password");

        String json = JsonUtil.toJson(request);

        assertNotNull(json);
        assertTrue(json.contains("Username"));
        assertTrue(json.contains("testuser"));
    }

    @Test
    void testFromJson_ValidJson_ShouldReturnObject() {
        String json = "{\"Username\":\"testuser\",\"Password\":\"password\"}";

        LoginRequest result = JsonUtil.fromJson(json, LoginRequest.class);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("password", result.getPassword());
    }

    @Test
    void testFromJson_InvalidJson_ShouldThrowException() {
        String invalidJson = "{invalid json}";

        assertThrows(RuntimeException.class, () -> {
            JsonUtil.fromJson(invalidJson, LoginRequest.class);
        });
    }
}
