package at.fhtw.swen1.mrp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO für Rating Creation/Update
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingRequest {
    private Integer score; // 1-5
    private String comment;
}

