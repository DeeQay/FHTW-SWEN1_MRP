package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.entity.Media;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MediaService {
    private static final Map<Long, Media> mediaStore = new ConcurrentHashMap<>();
    private static long mediaIdCounter = 1L;

    public Media createMedia(String title, String description, String mediaType, Integer releaseYear, List<String> genres, String ageRestriction) {
        Media media = new Media();
        media.setId(mediaIdCounter++);
        media.setTitle(title);
        media.setDescription(description);
        media.setMediaType(mediaType);
        media.setReleaseYear(releaseYear);
        media.setGenres(genres);
        media.setAgeRestriction(ageRestriction);
        media.setCreatedAt(LocalDateTime.now());

        mediaStore.put(media.getId(), media);
        return media;
    }

    public List<Media> getAllMedia() {
        return new ArrayList<>(mediaStore.values());
    }

    public Media getMediaById(Long id) {
        Media media = mediaStore.get(id);
        if (media == null) {
            throw new IllegalArgumentException("Media nicht gefunden");
        }
        return media;
    }

    public Media updateMedia(Long id, String title, String description, String mediaType, Integer releaseYear, List<String> genres, String ageRestriction) {
        Media media = mediaStore.get(id);
        if (media == null) {
            throw new IllegalArgumentException("Media nicht gefunden");
        }

        media.setTitle(title);
        media.setDescription(description);
        media.setMediaType(mediaType);
        media.setReleaseYear(releaseYear);
        media.setGenres(genres);
        media.setAgeRestriction(ageRestriction);

        mediaStore.put(id, media);
        return media;
    }

    public void deleteMedia(Long id) {
        if (!mediaStore.containsKey(id)) {
            throw new IllegalArgumentException("Media nicht gefunden");
        }
        mediaStore.remove(id);
    }
}

