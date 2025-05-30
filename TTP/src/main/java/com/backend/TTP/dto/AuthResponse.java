package com.backend.TTP.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    
    // Explicit getters/setters if needed, but @Data should handle these too
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
}