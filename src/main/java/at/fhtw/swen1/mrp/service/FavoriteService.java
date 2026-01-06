package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.dao.FavoriteDAO;
import at.fhtw.swen1.mrp.dao.MediaDAO;
import at.fhtw.swen1.mrp.entity.Favorite;
import at.fhtw.swen1.mrp.entity.Media;
import at.fhtw.swen1.mrp.util.DatabaseConnection;

import java.util.List;

/**
 * Service für Favorites Business Logic
 */
public class FavoriteService {

    private final FavoriteDAO favoriteDAO;
    private final MediaDAO mediaDAO;

    public FavoriteService() {
        this.favoriteDAO = new FavoriteDAO();
        this.mediaDAO = new MediaDAO();
    }

    // Media als Favorit markieren
    public Favorite addFavorite(Long userId, Long mediaId) {
        return DatabaseConnection.executeInTransaction(conn -> {
            // Prüfen ob Media existiert
            Media media = mediaDAO.findById(conn, mediaId);
            if (media == null) {
                throw new IllegalArgumentException("Media nicht gefunden");
            }

            // Prüfen ob bereits Favorit
            if (favoriteDAO.existsByUserAndMedia(conn, userId, mediaId)) {
                throw new IllegalStateException("Media ist bereits als Favorit markiert");
            }

            Favorite favorite = new Favorite(userId, mediaId);
            favoriteDAO.save(conn, favorite);
            return favorite;
        });
    }

    // Favorit entfernen
    public void removeFavorite(Long userId, Long mediaId) {
        DatabaseConnection.executeInTransactionVoid(conn -> {
            // Prüfen ob Favorit existiert
            if (!favoriteDAO.existsByUserAndMedia(conn, userId, mediaId)) {
                throw new IllegalStateException("Media ist nicht als Favorit markiert");
            }

            favoriteDAO.deleteByUserAndMedia(conn, userId, mediaId);
        });
    }

    // Favoriten-Liste eines Users
    public List<Favorite> getFavoritesByUserId(Long userId) {
        return DatabaseConnection.executeInTransaction(conn -> favoriteDAO.findByUserId(conn, userId));
    }

    // Prüfen ob Media Favorit ist
    public boolean isFavorite(Long userId, Long mediaId) {
        return DatabaseConnection.executeInTransaction(conn ->
                favoriteDAO.existsByUserAndMedia(conn, userId, mediaId));
    }
}

