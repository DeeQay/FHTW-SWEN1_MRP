package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.dao.MediaDAO;
import at.fhtw.swen1.mrp.entity.Media;
import at.fhtw.swen1.mrp.util.DatabaseConnection;

import java.time.LocalDateTime;
import java.util.List;

public class MediaService {

    private final MediaDAO mediaDAO;

    public MediaService() {
        this.mediaDAO = new MediaDAO();
    }

    // Constructor für Tests
    public MediaService(MediaDAO mediaDAO) {
        this.mediaDAO = mediaDAO;
    }

    public Media createMedia(String title, String description, String mediaType, Integer releaseYear, List<String> genres, String ageRestriction, Long creatorId) {
        return DatabaseConnection.executeInTransaction(conn -> {
            Media media = new Media();
            media.setTitle(title);
            media.setDescription(description);
            media.setMediaType(mediaType);
            media.setReleaseYear(releaseYear);
            media.setGenres(genres);
            media.setAgeRestriction(ageRestriction);
            media.setCreatorId(creatorId);
            media.setCreatedAt(LocalDateTime.now());

            mediaDAO.save(conn, media);
            return media;
        });
    }

    public List<Media> getAllMedia() {
        return DatabaseConnection.executeInTransaction(mediaDAO::findAll);
    }

    // Alle Parameter sind optional (null = kein Filter).
    public List<Media> searchMedia(String title, String genre, String mediaType,
                                   Integer releaseYear, String ageRestriction, Double minRating, String sortBy) {
        return DatabaseConnection.executeInTransaction(conn ->
            mediaDAO.findWithFilters(conn, title, genre, mediaType, releaseYear, ageRestriction, minRating, sortBy)
        );
    }

    public Media getMediaById(Long id) {
        return DatabaseConnection.executeInTransaction(conn -> {
            Media media = mediaDAO.findById(conn, id);
            if (media == null) {
                throw new IllegalArgumentException("Media nicht gefunden");
            }
            return media;
        });
    }

    public Media updateMedia(Long id, String title, String description, String mediaType, Integer releaseYear, List<String> genres, String ageRestriction, Long userId) {
        return DatabaseConnection.executeInTransaction(conn -> {
            Media media = mediaDAO.findById(conn, id);
            if (media == null) {
                throw new IllegalArgumentException("Media nicht gefunden");
            }

            // Ownership Check: nur Creator darf bearbeiten
            if (media.getCreatorId() == null || !media.getCreatorId().equals(userId)) {
                throw new SecurityException("Nur der Creator darf dieses Media bearbeiten");
            }

            media.setTitle(title);
            media.setDescription(description);
            media.setMediaType(mediaType);
            media.setReleaseYear(releaseYear);
            media.setGenres(genres);
            media.setAgeRestriction(ageRestriction);

            mediaDAO.update(conn, media);
            return media;
        });
    }

    public void deleteMedia(Long id, Long userId) {
        DatabaseConnection.executeInTransactionVoid(conn -> {
            Media media = mediaDAO.findById(conn, id);
            if (media == null) {
                throw new IllegalArgumentException("Media nicht gefunden");
            }

            // Ownership Check: nur Creator darf löschen
            if (media.getCreatorId() == null || !media.getCreatorId().equals(userId)) {
                throw new SecurityException("Nur der Creator darf dieses Media löschen");
            }

            mediaDAO.delete(conn, id);
        });
    }
}

