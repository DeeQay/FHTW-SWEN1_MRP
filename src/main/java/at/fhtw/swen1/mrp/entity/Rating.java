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

    // Constructor: userId, mediaId, score, comment, (Rest = Default)
    public Rating(Long userId, Long mediaId, Integer score, String comment) {
        this.userId = userId;
        this.mediaId = mediaId;
        this.score = score;
        this.comment = comment;
        this.isConfirmed = false; // default: false
        this.likeCount = 0; // default: 0
        this.createdAt = LocalDateTime.now(); // default: now
        this.updatedAt = LocalDateTime.now(); // default: now
    }

    // Constructor: + isConfirmed
    public Rating(Long userId, Long mediaId, Integer score, String comment, Boolean isConfirmed) {
        this(userId, mediaId, score, comment);
        this.isConfirmed = isConfirmed;
    }

    // Constructor: + isConfirmed, likeCount
    public Rating(Long userId, Long mediaId, Integer score, String comment, Boolean isConfirmed, Integer likeCount) {
        this(userId, mediaId, score, comment);
        this.isConfirmed = isConfirmed;
        this.likeCount = likeCount;
    }
}

