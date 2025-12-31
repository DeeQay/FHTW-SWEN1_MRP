package at.fhtw.swen1.mrp.server;

import at.fhtw.swen1.mrp.controller.AuthController;
import at.fhtw.swen1.mrp.controller.MediaController;
import at.fhtw.swen1.mrp.controller.RatingController;
import at.fhtw.swen1.mrp.controller.UserController;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class HttpServer {
    private final com.sun.net.httpserver.HttpServer server;
    private final AuthController authController;
    private final MediaController mediaController;
    private final RatingController ratingController;
    private final UserController userController;

    public HttpServer() throws IOException {
        this.server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(8080), 0);
        this.authController = new AuthController();
        this.mediaController = new MediaController();
        this.ratingController = new RatingController();
        this.userController = new UserController();

        configureRoutes();
        server.setExecutor(Executors.newFixedThreadPool(10));
    }

    private void configureRoutes() {
        // Authentifizierung
        server.createContext("/api/users/register", authController::handleRegister);
        server.createContext("/api/users/login", authController::handleLogin);
        server.createContext("/api/users", userController::handleUser);

        // Rating Routes
        server.createContext("/api/media/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            if (path.matches("/api/media/\\d+/rate")) {
                ratingController.handleRating(exchange);
            } else {
                mediaController.handleMedia(exchange);
            }
        });
        server.createContext("/api/ratings", ratingController::handleRating);
    }

    public void start() {
        server.start();
        System.out.println("Server started on port 8080");
    }

    public void stop() {
        server.stop(0);
        System.out.println("Server stopped");
    }
}
