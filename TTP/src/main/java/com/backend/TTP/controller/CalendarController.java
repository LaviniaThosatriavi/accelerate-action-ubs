package com.backend.TTP.controller;

import com.backend.TTP.dto.CalendarEventDTO;
import com.backend.TTP.dto.CalendarMonthDTO;
import com.backend.TTP.model.User;
import com.backend.TTP.repository.UserRepository;
import com.backend.TTP.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
            Authentication auth,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Default to current month if not specified
        LocalDate today = LocalDate.now();
        int requestedYear = year != null ? year : today.getYear();
        int requestedMonth = month != null ? month : today.getMonthValue();
        
        CalendarMonthDTO calendarMonth = calendarService.getCalendarMonth(user, requestedYear, requestedMonth);
        
        return ResponseEntity.ok(calendarMonth);
    }
    
    @PostMapping("/event")
    public ResponseEntity<CalendarEventDTO> createCalendarEvent(
            Authentication auth,
            @RequestBody CalendarEventDTO eventDTO) {
        
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        CalendarEventDTO created = calendarService.createCalendarEvent(user, eventDTO);
        
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/event/{id}")
    public ResponseEntity<CalendarEventDTO> updateCalendarEvent(
            Authentication auth,
            @PathVariable Long id,
            @RequestBody CalendarEventDTO eventDTO) {
        
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        CalendarEventDTO updated = calendarService.updateCalendarEvent(user, id, eventDTO);
        
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/event/{id}")
    public ResponseEntity<Void> deleteCalendarEvent(
            Authentication auth,
            @PathVariable Long id) {
        
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        calendarService.deleteCalendarEvent(user, id);
        
        return ResponseEntity.noContent().build();
    }
}