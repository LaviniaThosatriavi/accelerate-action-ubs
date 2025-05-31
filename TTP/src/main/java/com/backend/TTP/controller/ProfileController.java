package com.backend.TTP.controller;

import com.backend.TTP.dto.ProfileRequest;
import com.backend.TTP.dto.ProfileResponse;
import com.backend.TTP.model.User;
import com.backend.TTP.service.ProfileService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true",
        allowedHeaders = {"Authorization", "Content-Type"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    private final ProfileService profileService;

    @PostMapping
    public ResponseEntity<ProfileResponse> createProfile(
            @RequestBody ProfileRequest request,
            @AuthenticationPrincipal User user) {
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
    public ResponseEntity<ProfileResponse> getProfile(
            @AuthenticationPrincipal User user) {
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
    public ResponseEntity<ProfileResponse> updateProfile(
            @RequestBody ProfileRequest request,
            @AuthenticationPrincipal User user) {
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
    public ResponseEntity<?> handleOptions() {
        return ResponseEntity.ok().build();
    }
}