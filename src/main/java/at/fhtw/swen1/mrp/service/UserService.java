package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.entity.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * UserService verwaltet User-bezogene Business Logic
 */
public class UserService {
    private static final Map<String, User> userStore = new ConcurrentHashMap<>();
    private static long userIdCounter = 1L;

    public UserService() {
        // Default constructor
    }

    public User registerUser(String username, String password, String email) {
        if (userStore.containsKey(username)) {
            throw new IllegalArgumentException("Username bereits vorhanden");
        }

        String hashedPassword = hashPassword(password);
        User user = new User();
        user.setId(userIdCounter++);
        user.setUsername(username);
        user.setPasswordHash(hashedPassword);
        user.setEmail(email);
        user.setCreatedAt(LocalDateTime.now());

        userStore.put(username, user);
        return user;
    }

    public User loginUser(String username, String password) {
        User user = userStore.get(username);
        if (user == null) {
            throw new IllegalArgumentException("Ungültige Credentials");
        }

        String hashedPassword = hashPassword(password);
        if (!user.getPasswordHash().equals(hashedPassword)) {
            throw new IllegalArgumentException("Ungültige Credentials");
        }

        return user;
    }

    public User getUserByUsername(String username) {
        return userStore.get(username);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 nicht verfügbar", e);
        }
    }
}

