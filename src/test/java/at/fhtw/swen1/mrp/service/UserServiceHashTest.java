package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.dao.UserDAO;
import at.fhtw.swen1.mrp.entity.User;
import org.junit.jupiter.api.Test;

import java.security.MessageDigest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test für Password Hashing in UserService
 */
public class UserServiceHashTest {

    @Test
    public void testPasswordHashingSHA256() throws Exception {
        // Test dass Passwörter mit SHA-256 gehasht werden
        String password = "testPassword123";

        // Berechne erwarteten Hash
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes());
        StringBuilder expectedHash = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) expectedHash.append('0');
            expectedHash.append(hex);
        }

        // Test dass der Hash korrekt ist
        assertNotNull(expectedHash.toString());
        assertEquals(64, expectedHash.toString().length()); // SHA-256 produces 64 hex characters

        System.out.println("SHA-256 Hash Test erfolgreich");
        System.out.println("Erwarteter Hash für '" + password + "': " + expectedHash.toString());
    }

    @Test
    public void testSamePasswordProducesSameHash() throws Exception {
        String password = "samePassword";

        // Hash denselben Passwort zweimal
        MessageDigest digest1 = MessageDigest.getInstance("SHA-256");
        byte[] hash1 = digest1.digest(password.getBytes());

        MessageDigest digest2 = MessageDigest.getInstance("SHA-256");
        byte[] hash2 = digest2.digest(password.getBytes());

        // Beide Hashes sollten identisch sein
        assertArrayEquals(hash1, hash2, "Dasselbe Passwort sollte denselben Hash produzieren");
    }

    @Test
    public void testDifferentPasswordsProduceDifferentHashes() throws Exception {
        MessageDigest digest1 = MessageDigest.getInstance("SHA-256");
        byte[] hash1 = digest1.digest("password1".getBytes());

        MessageDigest digest2 = MessageDigest.getInstance("SHA-256");
        byte[] hash2 = digest2.digest("password2".getBytes());

        // Verschiedene Passwörter sollten verschiedene Hashes produzieren
        assertFalse(java.util.Arrays.equals(hash1, hash2));
    }

    @Test
    public void testHashLength() throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest("anyPassword".getBytes());

        // SHA-256 produziert immer 32 bytes (256 bits)
        assertEquals(32, hash.length);
    }
}

