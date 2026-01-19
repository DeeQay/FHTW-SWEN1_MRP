package at.fhtw.swen1.mrp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO für Media Ratings mit Average Score
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaRatingsResponse {
    private Double averageRating;
    private Integer totalRatings;
    private List<RatingResponse> ratings;
}
