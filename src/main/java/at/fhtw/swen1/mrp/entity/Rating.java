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
    private Integer score; // z.B. 1-5 oder 1-10
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Rating(Long userId, Long mediaId, Integer score, String comment) {
        this.userId = userId;
        this.mediaId = mediaId;
        this.score = score;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // TODO: Validierung hinzufügen (score range check)
    // TODO: Prevent duplicate ratings (unique constraint in DB)
}

