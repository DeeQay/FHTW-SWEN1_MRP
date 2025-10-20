package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.dao.MediaDAO;
import at.fhtw.swen1.mrp.entity.Media;

import java.util.List;

/**
 * MediaService verwaltet Geschäftslogik für Media-Operationen
 * Zwischenabgabe - grundlegende CRUD Operationen mit Stubs
 */
public class MediaService {
    private final MediaDAO mediaDAO;

    public MediaService() {
        // TODO: Mit ordnungsgemäßer Dependency Injection in final submission initialisieren
        this.mediaDAO = new MediaDAO();
    }

    public Media createMedia(String title, String description, String mediaType, Integer releaseYear) {
        // TODO: Ordnungsgemäße Validierung implementieren
        // TODO: Auf doppelte Titel prüfen
        // TODO: Media Type und Release Year validieren

        // Stub implementation
        Media media = new Media(title, description, mediaType, releaseYear);

        // TODO: mediaDAO.create(media) aufrufen
        return media;
    }

    public List<Media> getAllMedia() {
        // TODO: Tatsächlichen Database-Abruf implementieren
        // TODO: Pagination Support hinzufügen
        // TODO: Filtering-Funktionen hinzufügen

        // Stub implementation - leere Liste für jetzt zurückgeben
        return List.of();
    }

    public Media getMediaById(Long id) {
        // TODO: Database-Abruf nach ID implementieren
        // TODO: Media not found behandeln

        // Stub implementation
        Media media = new Media("Media " + id, "Beschreibung", "Movie", 2024);
        media.setId(id);
        return media;
    }

    public Media updateMedia(Long id, String title, String description, String mediaType, Integer releaseYear) {
        // TODO: Media Update in Database implementieren
        // TODO: Validieren dass Media existiert
        // TODO: Input data validieren

        // Stub implementation
        Media media = new Media(title, description, mediaType, releaseYear);
        media.setId(id);

        return media;
    }

    public boolean deleteMedia(Long id) {
        // TODO: Media Delete aus Database implementieren
        // TODO: Validieren dass Media existiert
        // TODO: Cascade deletes für Related data behandeln

        // Stub implementation
        return true;
    }

    // TODO: Methoden für Search, Filtering, Rating hinzufügen
}

