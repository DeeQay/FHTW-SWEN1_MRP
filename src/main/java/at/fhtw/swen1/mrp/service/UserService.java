package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.dao.UserDAO;
import at.fhtw.swen1.mrp.entity.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 * UserService verwaltet User-bezogene Business Logic
 * Zwischenabgabe - grundlegende User Management Funktionen
 */
public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User registerUser(String username, String password, String email) throws SQLException {
        if (userDAO.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username bereits vorhanden");
        }

        String hashedPassword = hashPassword(password);
        User user = new User(username, hashedPassword, email);
        userDAO.create(user);
        return user;
    }

    public User loginUser(String username, String password) throws SQLException {
        User user = userDAO.findByUsername(username).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("Ungültige Credentials");
        }

        String hashedPassword = hashPassword(password);
        if (!user.getPasswordHash().equals(hashedPassword)) {
            throw new IllegalArgumentException("Ungültige Credentials");
        }

        return user;
    }

    public User getUserByUsername(String username) throws SQLException {
        return userDAO.findByUsername(username).orElse(null);
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

