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

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping
    public ResponseEntity<ProfileResponse> createProfile(
            @RequestBody ProfileRequest request,
            @AuthenticationPrincipal User user) {
        try {
            return ResponseEntity.ok(
                profileService.createOrUpdateProfile(request, user.getUsername())
            );
        } catch (Exception e) {
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
            return ResponseEntity.ok(
                profileService.getProfile(user.getUsername())
            );
        } catch (RuntimeException e) {
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
            return ResponseEntity.ok(
                profileService.createOrUpdateProfile(request, user.getUsername())
            );
        } catch (Exception e) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error updating profile: " + e.getMessage(), 
                e
            );
        }
    }
}