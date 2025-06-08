package com.backend.TTP.service;

import com.backend.TTP.dto.CalendarEventDTO;
import com.backend.TTP.dto.CalendarMonthDTO;
import com.backend.TTP.model.CalendarEvent;
import com.backend.TTP.model.EnrolledCourse;
import com.backend.TTP.model.User;
import com.backend.TTP.repository.CalendarEventRepository;
import com.backend.TTP.repository.EnrolledCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CalendarService {
    
    @Autowired
    private CalendarEventRepository calendarEventRepository;
    
    @Autowired
    private EnrolledCourseRepository enrolledCourseRepository;
    
    public CalendarMonthDTO getCalendarMonth(User user, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();
        
        List<CalendarEvent> events = calendarEventRepository.findByUserIdAndYearAndMonth(user.getId(), year, month);
        
        // Convert to DTOs and group by date
        Map<String, List<CalendarEventDTO>> eventsByDate = new HashMap<>();
        
        for (CalendarEvent event : events) {
            String dateKey = event.getEventDate().toString();
            
            CalendarEventDTO dto = new CalendarEventDTO();
            dto.setId(event.getId());
            dto.setEventDate(event.getEventDate());
            dto.setTitle(event.getTitle());
            dto.setDescription(event.getDescription());
            dto.setEventType(event.getEventType());
            
            if (event.getEnrolledCourse() != null) {
                dto.setEnrolledCourseId(event.getEnrolledCourse().getId());
                dto.setCourseTitle(event.getEnrolledCourse().getCourse().getTitle());
            }
            
            if (!eventsByDate.containsKey(dateKey)) {
                eventsByDate.put(dateKey, new ArrayList<>());
            }
            eventsByDate.get(dateKey).add(dto);
        }
        
        CalendarMonthDTO result = new CalendarMonthDTO();
        result.setYear(year);
        result.setMonth(month);
        result.setFirstDay(firstDay);
        result.setLastDay(lastDay);
        result.setEvents(eventsByDate);
        
        return result;
    }
    
    public CalendarEventDTO createCalendarEvent(User user, CalendarEventDTO eventDTO) {
        CalendarEvent event = new CalendarEvent();
        event.setUser(user);
        event.setEventDate(eventDTO.getEventDate());
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setEventType(eventDTO.getEventType());
        
        // Set the relationship with enrolled course if provided
        if (eventDTO.getEnrolledCourseId() != null) {
            Optional<EnrolledCourse> courseOpt = enrolledCourseRepository.findById(eventDTO.getEnrolledCourseId());
            
            if (courseOpt.isPresent()) {
                EnrolledCourse course = courseOpt.get();
                
                // Verify the course belongs to the user
                if (!course.getUser().getId().equals(user.getId())) {
                    throw new RuntimeException("Course with ID " + eventDTO.getEnrolledCourseId() + 
                            " does not belong to the current user");
                }
                
                event.setEnrolledCourse(course);
                
                // If no title provided, use course name
                if (event.getTitle() == null || event.getTitle().isEmpty()) {
                    event.setTitle(course.getCourse().getTitle() + " - " + event.getEventType());
                }
            } else {
                throw new RuntimeException("Course with ID " + eventDTO.getEnrolledCourseId() + " not found");
            }
        }
        
        CalendarEvent saved = calendarEventRepository.save(event);
        
        // Convert back to DTO
        eventDTO.setId(saved.getId());
        
        // Set course title in DTO if available
        if (saved.getEnrolledCourse() != null) {
            eventDTO.setCourseTitle(saved.getEnrolledCourse().getCourse().getTitle());
        }
        
        return eventDTO;
    }
    
    public void deleteCalendarEvent(User user, Long eventId) {
        Optional<CalendarEvent> eventOpt = calendarEventRepository.findById(eventId);
        
        if (eventOpt.isPresent()) {
            CalendarEvent event = eventOpt.get();
            
            // Verify the event belongs to the user
            if (!event.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Event with ID " + eventId + 
                        " does not belong to the current user");
            }
            
            calendarEventRepository.delete(event);
        } else {
            throw new RuntimeException("Event with ID " + eventId + " not found");
        }
    }
    
    public CalendarEventDTO updateCalendarEvent(User user, Long eventId, CalendarEventDTO eventDTO) {
        Optional<CalendarEvent> eventOpt = calendarEventRepository.findById(eventId);
        
        if (eventOpt.isPresent()) {
            CalendarEvent event = eventOpt.get();
            
            // Verify the event belongs to the user
            if (!event.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Event with ID " + eventId + 
                        " does not belong to the current user");
            }
            
            // Update fields
            event.setEventDate(eventDTO.getEventDate());
            event.setTitle(eventDTO.getTitle());
            event.setDescription(eventDTO.getDescription());
            event.setEventType(eventDTO.getEventType());
            
            // Update enrolled course relationship if changed
            if (eventDTO.getEnrolledCourseId() != null) {
                if (event.getEnrolledCourse() == null || 
                        !event.getEnrolledCourse().getId().equals(eventDTO.getEnrolledCourseId())) {
                    
                    Optional<EnrolledCourse> courseOpt = enrolledCourseRepository.findById(eventDTO.getEnrolledCourseId());
                    
                    if (courseOpt.isPresent()) {
                        EnrolledCourse course = courseOpt.get();
                        
                        // Verify the course belongs to the user
                        if (!course.getUser().getId().equals(user.getId())) {
                            throw new RuntimeException("Course with ID " + eventDTO.getEnrolledCourseId() + 
                                    " does not belong to the current user");
                        }
                        
                        event.setEnrolledCourse(course);
                    } else {
                        throw new RuntimeException("Course with ID " + eventDTO.getEnrolledCourseId() + " not found");
                    }
                }
            } else {
                // Remove relationship if ID is null
                event.setEnrolledCourse(null);
            }
            
            CalendarEvent saved = calendarEventRepository.save(event);
            
            // Update DTO
            eventDTO.setId(saved.getId());
            
            // Set course title in DTO if available
            if (saved.getEnrolledCourse() != null) {
                eventDTO.setCourseTitle(saved.getEnrolledCourse().getCourse().getTitle());
            } else {
                eventDTO.setCourseTitle(null);
            }
            
            return eventDTO;
        } else {
            throw new RuntimeException("Event with ID " + eventId + " not found");
        }
    }
    
    /**
     * Creates deadline events for a newly enrolled course
     */
    public void createCourseDeadlineEvents(User user, EnrolledCourse course) {
        if (course.getTargetCompletionDate() != null) {
            // Create deadline event
            CalendarEvent deadlineEvent = new CalendarEvent();
            deadlineEvent.setUser(user);
            deadlineEvent.setEventDate(course.getTargetCompletionDate());
            deadlineEvent.setTitle(course.getCourse().getTitle() + " - Target Completion");
            deadlineEvent.setDescription("Target completion date for " + course.getCourse().getTitle());
            deadlineEvent.setEventType("DEADLINE");
            deadlineEvent.setEnrolledCourse(course);
            
            calendarEventRepository.save(deadlineEvent);
            
            // Optionally create milestone events (e.g., 25%, 50%, 75% completion)
            LocalDate startDate = course.getEnrollmentDate() != null ? 
                    course.getEnrollmentDate() : LocalDate.now();
            LocalDate endDate = course.getTargetCompletionDate();
            
            if (startDate != null && endDate != null && startDate.isBefore(endDate)) {
                long totalDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
                
                if (totalDays > 14) { // Only create milestones for longer courses
                    // 25% milestone
                    createMilestone(user, course, startDate.plusDays(totalDays / 4), "25% Completion");
                    
                    // 50% milestone
                    createMilestone(user, course, startDate.plusDays(totalDays / 2), "50% Completion");
                    
                    // 75% milestone
                    createMilestone(user, course, startDate.plusDays(totalDays * 3 / 4), "75% Completion");
                }
            }
        }
    }
    
    private void createMilestone(User user, EnrolledCourse course, LocalDate date, String milestone) {
        CalendarEvent milestoneEvent = new CalendarEvent();
        milestoneEvent.setUser(user);
        milestoneEvent.setEventDate(date);
        milestoneEvent.setTitle(course.getCourse().getTitle() + " - " + milestone);
        milestoneEvent.setDescription("Suggested milestone: " + milestone + " for " + course.getCourse().getTitle());
        milestoneEvent.setEventType("MILESTONE");
        milestoneEvent.setEnrolledCourse(course);
        
        calendarEventRepository.save(milestoneEvent);
    }
}