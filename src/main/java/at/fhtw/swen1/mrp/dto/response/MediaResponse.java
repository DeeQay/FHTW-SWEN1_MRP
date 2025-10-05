package at.fhtw.swen1.mrp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO für Media Information
 * Verwendet für media-bezogene Endpoint Responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaResponse {
    private Long id;
    private String title;
    private String description;
    private String mediaType;
    private Integer releaseYear;
    private List<String> genres;
    private String ageRestriction;
    private LocalDateTime createdAt;


    // TODO: Add rating statistics for final submission
    // TODO: Add review count, average rating, etc.
}