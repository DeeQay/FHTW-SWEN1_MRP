package at.fhtw.swen1.mrp.controller;

import at.fhtw.swen1.mrp.dto.request.LoginRequest;
import at.fhtw.swen1.mrp.dto.request.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests für UserController
 * Testet HTTP Request Handling für User Endpoints
 * Gesamt: 5 Test-Methoden
 */
class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController();
        // TODO: UserService Dependency mocken
    }

    @Test
    @Disabled("TODO: HTTP Exchange Mocking implementieren")
    void testHandleRegister_ValidRequest_ShouldReturn201() {
        // TODO: Gültige Registration Request testen
        // TODO: HTTP Status 201 verifizieren
        fail("Test nicht implementiert für intermediate submission");
    }

    @Test
    @Disabled("TODO: HTTP Exchange Mocking implementieren")
    void testHandleLogin_ValidCredentials_ShouldReturnToken() {
        // TODO: Gültige Login Credentials testen
        // TODO: Token Response verifizieren
        fail("Test nicht implementiert für intermediate submission");
    }

    @Test
    @Disabled("TODO: HTTP Exchange Mocking implementieren")
    void testHandleProfile_ValidRequest_ShouldReturnProfile() {
        // TODO: Profile Request testen
        // TODO: User Profile Response verifizieren
        fail("Test nicht implementiert für intermediate submission");
    }

    @Test
    @Disabled("TODO: HTTP Exchange Mocking implementieren")
    void testHandleRegister_InvalidMethod_ShouldReturn405() {
        // TODO: Ungültige HTTP Method testen
        // TODO: HTTP Status 405 verifizieren
        fail("Test nicht implementiert für intermediate submission");
    }

    @Test
    @Disabled("TODO: HTTP Exchange Mocking implementieren")
    void testHandleLogin_InvalidCredentials_ShouldReturn401() {
        // TODO: Ungültige Credentials testen
        // TODO: HTTP Status 401 verifizieren
        fail("Test nicht implementiert für intermediate submission");
    }
}
