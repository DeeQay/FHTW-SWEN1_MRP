package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.dao.MediaDAO;
import at.fhtw.swen1.mrp.entity.Media;

import java.time.LocalDateTime;
import java.util.List;
//import java.util.ArrayList;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;

public class MediaService {
    // OLD: In-Memory Storage (auskommentiert)
    //private static final Map<Long, Media> mediaStore = new ConcurrentHashMap<>();
    //private static long mediaIdCounter = 1L;

    // NEW: Database Access
    private final MediaDAO mediaDAO;

    public MediaService() {
        this.mediaDAO = new MediaDAO();
    }

    public Media createMedia(String title, String description, String mediaType, Integer releaseYear, List<String> genres, String ageRestriction) {
        Media media = new Media();
        media.setTitle(title);
        media.setDescription(description);
        media.setMediaType(mediaType);
        media.setReleaseYear(releaseYear);
        media.setGenres(genres);
        media.setAgeRestriction(ageRestriction);
        media.setCreatedAt(LocalDateTime.now());

        mediaDAO.save(media);
        return media;

        // OLD CODE (auskommentiert):
        //Media media = new Media();
        //media.setId(mediaIdCounter++);
        //media.setTitle(title);
        //media.setDescription(description);
        //media.setMediaType(mediaType);
        //media.setReleaseYear(releaseYear);
        //media.setGenres(genres);
        //media.setAgeRestriction(ageRestriction);
        //media.setCreatedAt(LocalDateTime.now());
        //mediaStore.put(media.getId(), media);
        //return media;
    }

    public List<Media> getAllMedia() {
        return mediaDAO.findAll();

        // OLD CODE (auskommentiert):
        //return new ArrayList<>(mediaStore.values());
    }

    public Media getMediaById(Long id) {
        Media media = mediaDAO.findById(id);
        if (media == null) {
            throw new IllegalArgumentException("Media nicht gefunden");
        }
        return media;

        // OLD CODE (auskommentiert):
        //Media media = mediaStore.get(id);
        //if (media == null) {
        //    throw new IllegalArgumentException("Media nicht gefunden");
        //}
        //return media;
    }

    public Media updateMedia(Long id, String title, String description, String mediaType, Integer releaseYear, List<String> genres, String ageRestriction) {
        Media media = mediaDAO.findById(id);
        if (media == null) {
            throw new IllegalArgumentException("Media nicht gefunden");
        }

        media.setTitle(title);
        media.setDescription(description);
        media.setMediaType(mediaType);
        media.setReleaseYear(releaseYear);
        media.setGenres(genres);
        media.setAgeRestriction(ageRestriction);

        mediaDAO.update(media);
        return media;

        // OLD CODE (auskommentiert):
        //Media media = mediaStore.get(id);
        //if (media == null) {
        //    throw new IllegalArgumentException("Media nicht gefunden");
        //}
        //media.setTitle(title);
        //media.setDescription(description);
        //media.setMediaType(mediaType);
        //media.setReleaseYear(releaseYear);
        //media.setGenres(genres);
        //media.setAgeRestriction(ageRestriction);
        //mediaStore.put(id, media);
        //return media;
    }

    public void deleteMedia(Long id) {
        Media media = mediaDAO.findById(id);
        if (media == null) {
            throw new IllegalArgumentException("Media nicht gefunden");
        }
        mediaDAO.delete(id);

        // OLD CODE (auskommentiert):
        //if (!mediaStore.containsKey(id)) {
        //    throw new IllegalArgumentException("Media nicht gefunden");
        //}
        //mediaStore.remove(id);
    }
}

