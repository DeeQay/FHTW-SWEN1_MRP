package at.fhtw.swen1.mrp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO für einen Leaderboard Eintrag
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntryResponse {
    private String username;
    private int ratingCount;
}
