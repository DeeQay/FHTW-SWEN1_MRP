package at.fhtw.swen1.mrp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Rating Entity für Database Representation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
    private Long id;
    private Long userId;
    private Long mediaId;
    private Integer score; // 1-5 Sterne
    private String comment;
    private Boolean isConfirmed; // Comment sichtbar erst nach Bestätigung
    private Integer likeCount; // Anzahl der Likes
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Rating(Long userId, Long mediaId, Integer score, String comment) {
        this.userId = userId;
        this.mediaId = mediaId;
        this.score = score;
        this.comment = comment;
        this.isConfirmed = false;
        this.likeCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}

