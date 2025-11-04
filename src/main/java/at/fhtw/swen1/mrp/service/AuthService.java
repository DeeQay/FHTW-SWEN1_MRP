package at.fhtw.swen1.mrp.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Authentifizierung und Token-Management
 */
public class AuthService {
    private static final Map<String, String> tokenStore = new ConcurrentHashMap<>();

    public String generateToken(String username) {
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
}

