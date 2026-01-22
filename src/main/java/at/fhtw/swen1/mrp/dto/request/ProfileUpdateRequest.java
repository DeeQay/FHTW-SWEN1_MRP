package at.fhtw.swen1.mrp.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO für Profile Update
 * Verwendet für PUT /api/users/{username}/profile
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {

    @JsonProperty("Email")
    private String email;
}
