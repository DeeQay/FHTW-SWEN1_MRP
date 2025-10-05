package at.fhtw.swen1.mrp.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO für User Login
 * Verwendet für POST /api/users/login
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @JsonProperty("Username")
    private String username;

    @JsonProperty("Password")
    private String password;

    // TODO: Input Validierung hinzufügen
    // TODO: Rate Limiting für Login-Versuche hinzufügen
}
