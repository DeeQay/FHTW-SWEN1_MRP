package at.fhtw.swen1.mrp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Favorite Entity für User-Media Favoriten
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Favorite {
    private Long id;
    private Long userId;
    private Long mediaId;
    private LocalDateTime createdAt;

    // Constructor: userId, mediaId (Rest = Default)
    public Favorite(Long userId, Long mediaId) {
        this.userId = userId;
        this.mediaId = mediaId;
        this.createdAt = LocalDateTime.now();
    }
}

