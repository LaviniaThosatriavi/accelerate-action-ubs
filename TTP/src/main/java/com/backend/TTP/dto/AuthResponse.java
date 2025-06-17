package com.backend.TTP.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication response containing JWT token")
public class AuthResponse {
    
    @Schema(description = "JWT token for authentication", 
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", 
            accessMode = Schema.AccessMode.READ_ONLY)
    private String token;

    // Explicit getters/setters if needed, but @Data should handle these too
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
}