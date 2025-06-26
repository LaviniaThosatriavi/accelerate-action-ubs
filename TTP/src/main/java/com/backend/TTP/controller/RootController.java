package com.backend.TTP.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Root", description = "Root endpoint for API status")
public class RootController {
    
    @GetMapping("/")
    @Operation(summary = "API Status", 
               description = "Returns the status of the TTP Backend API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "API is running successfully",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<Map<String, String>> root() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "TTP Backend API is running successfully! 🚀");
        response.put("status", "UP");
        response.put("timestamp", Instant.now().toString());
        response.put("version", "1.0.0");
        response.put("description", "This is the backend API for the TTP Learning Platform");
        response.put("swagger_docs", "/swagger-ui.html");
        response.put("api_docs", "/v3/api-docs");
        return ResponseEntity.ok(response);
    }
}