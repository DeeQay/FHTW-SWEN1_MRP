package at.fhtw.swen1.mrp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO für User Profile
 * Verwendet für GET /api/users/{username}/profile Response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private String username;
    private String email;
    private LocalDateTime createdAt;

    // TODO: User-Statistiken hinzufügen (Ratings Count, etc.) für final submission
    // HINWEIS: Niemals Password-Informationen in Response einschließen
}
