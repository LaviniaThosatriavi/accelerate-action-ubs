package com.backend.TTP.config;

import com.backend.TTP.model.UserProfile;
import com.backend.TTP.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(1) // Run before DatabaseInitializer
public class DatabaseFixer implements CommandLineRunner {
    
    @Autowired
    private UserProfileRepository userProfileRepository;
    
    @Override
    public void run(String... args) throws Exception {
        fixExistingUserProfiles();
    }
    
    private void fixExistingUserProfiles() {
        System.out.println("Checking for user profiles that need achievement field updates...");
        
        List<UserProfile> allProfiles = userProfileRepository.findAll();
        int updatedCount = 0;
        
        for (UserProfile profile : allProfiles) {
            boolean needsUpdate = false;
            
            // Fix null totalPoints
            if (profile.getTotalPoints() == null) {
                profile.setTotalPoints(0);
                needsUpdate = true;
            }
            
            // Fix null currentBadgeLevel
            if (profile.getCurrentBadgeLevel() == null || profile.getCurrentBadgeLevel().isEmpty()) {
                profile.setCurrentBadgeLevel("NOVICE");
                needsUpdate = true;
            }
            
            // Fix null loginStreak
            if (profile.getLoginStreak() == null) {
                profile.setLoginStreak(0);
                needsUpdate = true;
            }
            
            if (needsUpdate) {
                userProfileRepository.save(profile);
                updatedCount++;
                System.out.println("Updated profile for user: " + profile.getUser().getUsername());
            }
        }
        
        if (updatedCount > 0) {
            System.out.println("Fixed " + updatedCount + " user profiles with missing achievement data.");
        } else {
            System.out.println("All user profiles already have proper achievement data.");
        }
    }
}