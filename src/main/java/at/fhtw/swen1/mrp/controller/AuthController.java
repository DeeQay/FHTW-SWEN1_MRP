package at.fhtw.swen1.mrp.controller;

import at.fhtw.swen1.mrp.dto.request.RegisterRequest;
import at.fhtw.swen1.mrp.dto.request.LoginRequest;
import at.fhtw.swen1.mrp.dto.response.LoginResponse;
import at.fhtw.swen1.mrp.entity.User;
import at.fhtw.swen1.mrp.service.AuthService;
import at.fhtw.swen1.mrp.service.UserService;
import at.fhtw.swen1.mrp.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    public AuthController() {
        this.authService = new AuthService();
        this.userService = new UserService();
    }

    public void handleRegister(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }

        try {
            String requestBody = readRequestBody(exchange);
            RegisterRequest request = JsonUtil.fromJson(requestBody, RegisterRequest.class);

            User user = userService.registerUser(request.getUsername(), request.getPassword(), request.getEmail());

            sendResponse(exchange, 201, "{\"message\":\"User registered successfully\",\"username\":\"" + user.getUsername() + "\"}");
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }

    public void handleLogin(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }

        try {
            String requestBody = readRequestBody(exchange);
            LoginRequest request = JsonUtil.fromJson(requestBody, LoginRequest.class);

            User user = userService.loginUser(request.getUsername(), request.getPassword());
            String token = authService.generateToken(user.getUsername());

            LoginResponse response = new LoginResponse(token);
            sendResponse(exchange, 200, JsonUtil.toJson(response));
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 401, "{\"error\":\"Invalid credentials\"}");
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
