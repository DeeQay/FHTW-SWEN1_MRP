package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.dao.MediaDAO;
import at.fhtw.swen1.mrp.entity.Media;

import java.time.LocalDateTime;
import java.util.List;

public class MediaService {
    // OLD: Memory Map statt DAO mit Datenbank
    //private static final Map<Long, Media> mediaStore = new ConcurrentHashMap<>();
    //private static long mediaIdCounter = 1L;

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

        // OLD: Memory Map statt DAO mit Datenbank
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

        // OLD: Memory Map statt DAO mit Datenbank
        //return new ArrayList<>(mediaStore.values());
    }

    public Media getMediaById(Long id) {
        Media media = mediaDAO.findById(id);
        if (media == null) {
            throw new IllegalArgumentException("Media nicht gefunden");
        }
        return media;

        // OLD: Memory Map statt DAO mit Datenbank
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

        // OLD: Memory Map statt DAO mit Datenbank
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

        // OLD: Memory Map statt DAO mit Datenbank
        //if (!mediaStore.containsKey(id)) {
        //    throw new IllegalArgumentException("Media nicht gefunden");
        //}
        //mediaStore.remove(id);
    }
}

