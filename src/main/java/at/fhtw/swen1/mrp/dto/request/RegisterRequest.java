package at.fhtw.swen1.mrp.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO für User Registration
 * Verwendet für POST /api/users/register
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @JsonProperty("Username")
    private String username;

    @JsonProperty("Password")
    private String password;

    @JsonProperty("Email")
    private String email;

    // TODO: Input Validierung Annotationen hinzufügen
    // TODO: Password Confirmation Feld hinzufügen
}
