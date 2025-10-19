package at.fhtw.swen1.mrp.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AuthService verwaltet Authentifizierung und Token-Management
 * Zwischenabgabe - einfacher In-Memory Token Store
 */
public class AuthService {
    // TODO: Mit ordnungsgemäßem Token Store (Redis/Database) in final submission ersetzen
    private static final Map<String, String> tokenStore = new ConcurrentHashMap<>();

    public String generateToken(String username) {
        // TODO: Ordnungsgemäße JWT Token-Generierung implementieren
        String token = username + "-token-" + System.currentTimeMillis();
        tokenStore.put(token, username);
        return token;
    }

    public boolean validateToken(String token) {
        return token != null && tokenStore.containsKey(token);
    }

    public String getUsernameFromToken(String token) {
        return tokenStore.get(token);
    }

    public void invalidateToken(String token) {
        tokenStore.remove(token);
    }

    // TODO: Token Expiration Logic hinzufügen
    // TODO: Refresh Token Support hinzufügen
}

