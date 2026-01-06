package at.fhtw.swen1.mrp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO für Favorite Information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponse {
    private Long id;
    private Long userId;
    private Long mediaId;
    private LocalDateTime createdAt;
}

