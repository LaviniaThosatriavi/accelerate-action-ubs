package com.backend.TTP.controller;

import com.backend.TTP.dto.CalendarMonthDTO;
import com.backend.TTP.model.User;
import com.backend.TTP.repository.UserRepository;
import com.backend.TTP.service.CalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/calendar")
@Tag(name = "Calendar Management", description = "Operations related to calendar views and course deadline management")
@SecurityRequirement(name = "bearerAuth")
public class CalendarController {
    
    @Autowired
    private CalendarService calendarService;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/month")
    @Operation(summary = "Get calendar month view", 
               description = "Retrieve calendar data for a specific month including course deadlines and important dates. Defaults to current month if no parameters provided.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calendar month data retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CalendarMonthDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid year or month parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CalendarMonthDTO> getCalendarMonth(
            @Parameter(description = "Year for calendar view (defaults to current year)", 
                      example = "2024")
            @RequestParam(required = false) Integer year,
            @Parameter(description = "Month for calendar view (1-12, defaults to current month)", 
                      example = "6")
            @RequestParam(required = false) Integer month) {
        
        User user = getCurrentUser();
        
        // Default to current month if not specified
        LocalDate today = LocalDate.now();
        int requestedYear = year != null ? year : today.getYear();
        int requestedMonth = month != null ? month : today.getMonthValue();
        
        CalendarMonthDTO calendarMonth = calendarService.getCalendarMonthWithCourseDeadlines(
            user, requestedYear, requestedMonth);
        
        return ResponseEntity.ok(calendarMonth);
    }
    
    @Operation(hidden = true)
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}