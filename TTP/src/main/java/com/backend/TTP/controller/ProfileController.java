package com.backend.TTP.controller;

import com.backend.TTP.dto.ProfileRequest;
import com.backend.TTP.dto.ProfileResponse;
import com.backend.TTP.model.User;
import com.backend.TTP.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true",
        allowedHeaders = {"Authorization", "Content-Type"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@Tag(name = "User Profile Management", description = "Operations for managing user profiles, career stages, skills, and learning preferences")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    private final ProfileService profileService;

    @PostMapping
    @Operation(summary = "Create user profile", 
               description = "Create a new user profile with career stage, skills, goals, and learning preferences. Missing fields will be set to default values.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile created successfully",
                    content = @Content(schema = @Schema(implementation = ProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid profile data or missing required fields"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error - failed to create profile")
    })
    public ResponseEntity<ProfileResponse> createProfile(
            @Parameter(description = "Profile creation data", required = true)
            @Valid @RequestBody ProfileRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        try {
            if (user == null) {
                logger.error("Authentication principal is null");
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User not authenticated"
                );
            }

            // Log the incoming request for debugging
            logger.info("Creating/updating profile for user: {}", user.getUsername());
            logger.debug("Request body: {}", request);
            
            // Validate request fields with detailed error messages
            if (request == null) {
                logger.error("Request body is null");
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Request body is required"
                );
            }
            
            // Add default values if fields are missing
            if (request.getCareerStage() == null) {
                logger.warn("Career stage is null, defaulting to 'beginner'");
                request.setCareerStage("beginner");
            }
            
            if (request.getSkills() == null) {
                logger.warn("Skills list is null, initializing empty list");
                request.setSkills(java.util.Collections.emptyList());
            }
            
            if (request.getGoals() == null) {
                logger.warn("Goals is null, defaulting to 'improve skills'");
                request.setGoals("improve skills");
            }
            
            if (request.getHoursPerWeek() == null) {
                logger.warn("Hours per week is null, defaulting to 5");
                request.setHoursPerWeek(5);
            }

            ProfileResponse response = profileService.createOrUpdateProfile(request, user.getUsername());
            logger.info("Profile created/updated successfully for user: {}", user.getUsername());

            return ResponseEntity.ok(response);

        } catch (ResponseStatusException e) {
            // Just rethrow response status exceptions
            throw e;
        } catch (Exception e) {
            logger.error("Error creating profile: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error creating profile: " + e.getMessage(),
                    e
            );
        }
    }

    @GetMapping
    @Operation(summary = "Get user profile", 
               description = "Retrieve the authenticated user's profile information including career stage, skills, goals, and preferences")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProfileResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "404", description = "Profile not found for the authenticated user"),
            @ApiResponse(responseCode = "500", description = "Internal server error - failed to retrieve profile")
    })
    public ResponseEntity<ProfileResponse> getProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        try {
            if (user == null) {
                logger.error("Authentication principal is null");
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User not authenticated"
                );
            }

            logger.info("Retrieving profile for user: {}", user.getUsername());

            ProfileResponse response = profileService.getProfile(user.getUsername());
            logger.info("Profile retrieved successfully for user: {}", user.getUsername());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.error("Error retrieving profile: {}", e.getMessage(), e);

            if (e.getMessage().contains("not found")) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        e.getMessage(),
                        e
                );
            }

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error retrieving profile: " + e.getMessage(),
                    e
            );
        }
    }

    @PutMapping
    @Operation(summary = "Update user profile", 
               description = "Update the authenticated user's profile information. Missing fields will be set to default values.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                    content = @Content(schema = @Schema(implementation = ProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid profile data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error - failed to update profile")
    })
    public ResponseEntity<ProfileResponse> updateProfile(
            @Parameter(description = "Profile update data", required = true)
            @Valid @RequestBody ProfileRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        try {
            if (user == null) {
                logger.error("Authentication principal is null");
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User not authenticated"
                );
            }

            logger.info("Updating profile for user: {}", user.getUsername());
            logger.debug("Profile request: {}", request);

            // Add default values if fields are missing
            if (request.getCareerStage() == null) {
                request.setCareerStage("beginner");
            }
            
            if (request.getSkills() == null) {
                request.setSkills(java.util.Collections.emptyList());
            }
            
            if (request.getGoals() == null) {
                request.setGoals("improve skills");
            }
            
            if (request.getHoursPerWeek() == null) {
                request.setHoursPerWeek(5);
            }

            ProfileResponse response = profileService.createOrUpdateProfile(request, user.getUsername());
            logger.info("Profile updated successfully for user: {}", user.getUsername());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error updating profile: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error updating profile: " + e.getMessage(),
                    e
            );
        }
    }

    // Handle OPTIONS preflight requests
    @RequestMapping(method = RequestMethod.OPTIONS)
    @Operation(hidden = true)
    public ResponseEntity<?> handleOptions() {
        return ResponseEntity.ok().build();
    }
}