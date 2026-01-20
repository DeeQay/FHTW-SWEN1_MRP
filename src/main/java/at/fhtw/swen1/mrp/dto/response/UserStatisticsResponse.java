package at.fhtw.swen1.mrp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO für User Statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsResponse {
    private int totalRatings;      // Anzahl abgegebener Ratings
    private Double averageScore;    // Durchschnitt der vergebenen Scores
    private String favoriteGenre;   // Meistbewertetes Genre
}
