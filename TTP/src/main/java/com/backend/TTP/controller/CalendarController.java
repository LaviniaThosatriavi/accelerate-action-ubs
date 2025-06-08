package com.backend.TTP.controller;

import com.backend.TTP.dto.CalendarMonthDTO;
import com.backend.TTP.model.User;
import com.backend.TTP.repository.UserRepository;
import com.backend.TTP.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {
    
    @Autowired
    private CalendarService calendarService;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/month")
    public ResponseEntity<CalendarMonthDTO> getCalendarMonth(
            @RequestParam(required = false) Integer year,
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
    
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}