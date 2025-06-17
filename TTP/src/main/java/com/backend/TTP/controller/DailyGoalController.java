package com.backend.TTP.controller;

import com.backend.TTP.dto.CompleteGoalRequest;
import com.backend.TTP.dto.CreateDailyGoalRequest;
import com.backend.TTP.dto.DailyGoalDTO;
import com.backend.TTP.model.User;
import com.backend.TTP.repository.UserRepository;
import com.backend.TTP.service.DailyGoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
@Tag(name = "Daily Goal Management", description = "Operations for managing daily goals and tracking goal completion")
@SecurityRequirement(name = "bearerAuth")
public class DailyGoalController {
    
    @Autowired
    private DailyGoalService dailyGoalService;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/today")
    @Operation(summary = "Get today's goals", 
               description = "Retrieve all daily goals for the current date for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Today's goals retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DailyGoalDTO.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<DailyGoalDTO>> getTodaysGoals(
            @Parameter(hidden = true) Authentication auth) {
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<DailyGoalDTO> goals = dailyGoalService.getTodaysGoals(user);
        
        return ResponseEntity.ok(goals);
    }
    
    @GetMapping("/today/active")
    @Operation(summary = "Get active goals for today", 
               description = "Retrieve only the active (incomplete) daily goals for today for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active goals retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DailyGoalDTO.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<DailyGoalDTO>> getActiveGoals(
            @Parameter(hidden = true) Authentication auth) {
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<DailyGoalDTO> goals = dailyGoalService.getActiveGoals(user);
        
        return ResponseEntity.ok(goals);
    }
    
    @PostMapping
    @Operation(summary = "Create new daily goal", 
               description = "Create a new daily goal for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daily goal created successfully",
                    content = @Content(schema = @Schema(implementation = DailyGoalDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid goal data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DailyGoalDTO> createGoal(
            @Parameter(hidden = true) Authentication auth,
            @Parameter(description = "Daily goal creation data", required = true)
            @Valid @RequestBody CreateDailyGoalRequest request) {
        
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        DailyGoalDTO created = dailyGoalService.createUserGoal(user, request);
        
        return ResponseEntity.ok(created);
    }
    
    @PostMapping("/complete")
    @Operation(summary = "Complete multiple goals", 
               description = "Mark multiple daily goals as completed for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Goals completed successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DailyGoalDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid goal completion data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<DailyGoalDTO>> completeGoals(
            @Parameter(hidden = true) Authentication auth,
            @Parameter(description = "Goal completion data with list of goal IDs", required = true)
            @Valid @RequestBody CompleteGoalRequest request) {
        
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<DailyGoalDTO> completedGoals = dailyGoalService.completeGoals(user, request.getCompletedGoalIds());
        
        return ResponseEntity.ok(completedGoals);
    }
    
    @GetMapping("/week")
    @Operation(summary = "Get weekly goals", 
               description = "Retrieve daily goals for a specific week. Defaults to current week if no dates provided.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Weekly goals retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DailyGoalDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid date format"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<DailyGoalDTO>> getWeeklyGoals(
            @Parameter(hidden = true) Authentication auth,
            @Parameter(description = "Start date of the week (YYYY-MM-DD format, defaults to current week Monday)", 
                      example = "2024-06-17")
            @RequestParam(required = false) String startDate,
            @Parameter(description = "End date of the week (YYYY-MM-DD format, defaults to current week Sunday)", 
                      example = "2024-06-23")
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
    @Operation(summary = "Get goal by ID", 
               description = "Retrieve a specific daily goal by its ID for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Goal retrieved successfully",
                    content = @Content(schema = @Schema(implementation = DailyGoalDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "404", description = "Goal not found or user not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DailyGoalDTO> getGoalById(
            @Parameter(hidden = true) Authentication auth,
            @Parameter(description = "Goal ID", required = true, example = "123")
            @PathVariable Long id) {
            
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        DailyGoalDTO goal = dailyGoalService.getGoalById(user, id);
        
        return ResponseEntity.ok(goal);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete goal", 
               description = "Delete a specific daily goal by its ID for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Goal deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "404", description = "Goal not found or user not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteGoal(
            @Parameter(hidden = true) Authentication auth,
            @Parameter(description = "Goal ID to delete", required = true, example = "123")
            @PathVariable Long id) {
            
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        dailyGoalService.deleteGoal(user, id);
        
        return ResponseEntity.noContent().build();
    }
}