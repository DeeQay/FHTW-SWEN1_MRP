package at.fhtw.swen1.mrp.integration;

import at.fhtw.swen1.mrp.server.HttpServer;
import at.fhtw.swen1.mrp.util.JsonUtil;
import at.fhtw.swen1.mrp.dto.response.LoginResponse;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Tests für HTTP API Endpoints.
 * Testet die API von außen über HTTP-Requests.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApiIntegrationTest {

    private static HttpServer server;
    private static HttpClient client;
    private static final String BASE_URL = "http://localhost:8080";

    // Token wird zwischen Tests geteilt
    private static String authToken;

    @BeforeAll
    static void setUp() throws IOException {
        // Server starten
        server = new HttpServer();
        server.start();
        client = HttpClient.newHttpClient();
    }

    @AfterAll
    static void tearDown() {
        // Server stoppen
        if (server != null) {
            server.stop();
        }
    }

    @Test
    @Order(1)
    void testRegisterUser_ShouldReturn201() throws IOException, InterruptedException {
        // Arrange: Unique Username generieren
        String uniqueUsername = "integrationtest_" + System.currentTimeMillis();
        String requestBody = String.format(
            "{\"Username\":\"%s\",\"Password\":\"test123\",\"Email\":\"test@example.com\"}",
            uniqueUsername
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/users/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        // Act
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        assertEquals(201, response.statusCode(), "Register sollte 201 zurückgeben");
        assertTrue(response.body().contains("User registered successfully"),
                   "Response sollte Erfolgsmeldung enthalten");
    }

    @Test
    @Order(2)
    void testLoginUser_ShouldReturn200WithToken() throws IOException, InterruptedException {
        // Arrange: Bekannter Test-User (aus schema.sql)
        String requestBody = "{\"Username\":\"testuser\",\"Password\":\"test123\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/users/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        // Act
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "Login sollte 200 zurückgeben");

        // Token aus JSON Response extrahieren
        LoginResponse loginResponse = JsonUtil.fromJson(response.body(), LoginResponse.class);
        authToken = loginResponse.getToken();

        assertNotNull(authToken, "Token sollte extrahiert werden können");
        assertFalse(authToken.isEmpty(), "Token sollte nicht leer sein");
    }

    @Test
    @Order(3)
    void testGetMediaWithAuth_ShouldReturn200() throws IOException, InterruptedException {
        // Arrange: Token aus Login-Test verwenden
        assertNotNull(authToken, "Token muss aus vorherigem Test vorhanden sein");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/media"))
                .header("Authorization", "Bearer " + authToken)
                .GET()
                .build();

        // Act
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "GET /api/media sollte 200 zurückgeben");
        assertTrue(response.body().startsWith("["), "Response sollte JSON Array sein");
    }

    @Test
    @Order(4)
    void testGetMediaWithoutAuth_ShouldReturn401() throws IOException, InterruptedException {
        // Arrange: Kein Authorization Header

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/media"))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        assertEquals(401, response.statusCode(), "GET ohne Auth sollte 401 zurückgeben");
    }
}
