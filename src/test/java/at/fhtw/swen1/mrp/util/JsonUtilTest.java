package at.fhtw.swen1.mrp.util;

import at.fhtw.swen1.mrp.dto.request.LoginRequest;
import at.fhtw.swen1.mrp.dto.response.MediaResponse;
import org.junit.jupiter.api.Disabled;
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
        // TODO: Object zu JSON Konvertierung testen
        LoginRequest request = new LoginRequest("testuser", "password");

        String json = JsonUtil.toJson(request);

        assertNotNull(json);
        assertTrue(json.contains("Username"));
        assertTrue(json.contains("testuser"));
        // TODO: Spezifischere JSON Format-Validierung hinzufügen
    }

    @Test
    void testFromJson_ValidJson_ShouldReturnObject() {
        // TODO: JSON zu Object Konvertierung testen
        String json = "{\"Username\":\"testuser\",\"Password\":\"password\"}";

        LoginRequest result = JsonUtil.fromJson(json, LoginRequest.class);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("password", result.getPassword());
    }

    @Test
    @Disabled("TODO: Ordnungsgemäße Error Handling in final submission implementieren")
    void testToJson_NullObject_ShouldThrowException() {
        // TODO: Error Handling für null Input testen
        assertThrows(RuntimeException.class, () -> {
            JsonUtil.toJson(null);
        });
    }

    @Test
    void testFromJson_InvalidJson_ShouldThrowException() {
        // TODO: Error Handling für fehlerhaftes JSON testen
        String invalidJson = "{invalid json}";

        assertThrows(RuntimeException.class, () -> {
            JsonUtil.fromJson(invalidJson, LoginRequest.class);
        });
    }
}
