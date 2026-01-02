package at.fhtw.swen1.mrp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * RatingLike Entity für Like-Funktion auf Ratings
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingLike {
    private Long id;
    private Long userId;
    private Long ratingId;
    private LocalDateTime createdAt;

    // Constructor: userId, ratingId (Rest = Default)
    public RatingLike(Long userId, Long ratingId) {
        this.userId = userId;
        this.ratingId = ratingId;
        this.createdAt = LocalDateTime.now();
    }
}

