package at.fhtw.swen1.mrp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO für Media Creation/Update
 * Verwendet für POST/PUT /api/media Endpoints
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaRequest {
    private String title;
    private String description;
    private String mediaType;
    private Integer releaseYear;
    private List<String> genres;
    private String ageRestriction;

    // TODO: Input Validierung hinzufügen
    // TODO: Required Field Marker hinzufügen
}
