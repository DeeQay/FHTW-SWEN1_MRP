package at.fhtw.swen1.mrp.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Token-basierte Authentifizierung
 */
public class AuthService {
    // Token Speicher (in-memory)
    private static final Map<String, String> tokenStore = new HashMap<>();

    // Generiert einen Token für User
    public String generateToken(String username) {
        String token = username + "-token-" + System.currentTimeMillis();
        tokenStore.put(token, username);
        return token;
    }

    // Prüft ob Token gültig ist
    public boolean validateToken(String token) {
        return token != null && tokenStore.containsKey(token);
    }

    // Extrahiert Username aus Token
    public String getUsernameFromToken(String token) {
        return tokenStore.get(token);
    }

    // Token invalidieren (logout)
    public void invalidateToken(String token) {
        tokenStore.remove(token);
    }
}
