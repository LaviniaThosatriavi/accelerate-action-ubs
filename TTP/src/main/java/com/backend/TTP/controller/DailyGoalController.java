package com.backend.TTP.controller;

import com.backend.TTP.dto.CompleteGoalRequest;
import com.backend.TTP.dto.CreateDailyGoalRequest;
import com.backend.TTP.dto.DailyGoalDTO;
import com.backend.TTP.model.User;
import com.backend.TTP.repository.UserRepository;
import com.backend.TTP.service.DailyGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class DailyGoalController {
    
    @Autowired
    private DailyGoalService dailyGoalService;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/today")
    public ResponseEntity<List<DailyGoalDTO>> getTodaysGoals(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<DailyGoalDTO> goals = dailyGoalService.getTodaysGoals(user);
        
        return ResponseEntity.ok(goals);
    }
    
    @PostMapping
    public ResponseEntity<DailyGoalDTO> createGoal(
            Authentication auth,
            @RequestBody CreateDailyGoalRequest request) {
        
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        DailyGoalDTO created = dailyGoalService.createUserGoal(user, request);
        
        return ResponseEntity.ok(created);
    }
    
    @PostMapping("/complete")
    public ResponseEntity<List<DailyGoalDTO>> completeGoals(
            Authentication auth,
            @RequestBody CompleteGoalRequest request) {
        
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<DailyGoalDTO> completedGoals = dailyGoalService.completeGoals(user, request.getCompletedGoalIds());
        
        return ResponseEntity.ok(completedGoals);
    }
    
    @GetMapping("/week")
    public ResponseEntity<List<DailyGoalDTO>> getWeeklyGoals(
            Authentication auth,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
            
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : 
                LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : 
                start.plusDays(6); // Default to a 7-day week
        
        List<DailyGoalDTO> goals = dailyGoalService.getGoalsForDateRange(user, start, end);
        
        return ResponseEntity.ok(goals);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DailyGoalDTO> getGoalById(
            Authentication auth,
            @PathVariable Long id) {
            
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        DailyGoalDTO goal = dailyGoalService.getGoalById(user, id);
        
        return ResponseEntity.ok(goal);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(
            Authentication auth,
            @PathVariable Long id) {
            
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        dailyGoalService.deleteGoal(user, id);
        
        return ResponseEntity.noContent().build();
    }
}