package at.fhtw.swen1.mrp.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * JSON-Hilfsklasse für Serialisierung und Deserialisierung
 * Verwendet Jackson für JSON-Verarbeitung (erlaubte Bibliothek)
 */
public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // ObjectMapper konfigurieren
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        // TODO: Weitere Konfiguration für Produktion hinzufügen (Datumsformate, null-Behandlung, etc.)
    }

    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Serialisierung des Objekts zu JSON fehlgeschlagen", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Deserialisierung von JSON zu Objekt fehlgeschlagen", e);
        }
    }

    // TODO: Methoden für generische Typ-Behandlung hinzufügen (List<T>, Map<String, T>, etc.)
    // TODO: Fehlerbehandlung für fehlerhafte JSON hinzufügen
    // TODO: Validierungsunterstützung hinzufügen
}