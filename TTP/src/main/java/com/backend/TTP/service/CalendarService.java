package com.backend.TTP.service;

import com.backend.TTP.dto.CalendarEventDTO;
import com.backend.TTP.dto.CalendarMonthDTO;
import com.backend.TTP.model.EnrolledCourse;
import com.backend.TTP.model.User;
import com.backend.TTP.repository.EnrolledCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CalendarService {
    
    @Autowired
    private EnrolledCourseRepository enrolledCourseRepository;
    
    public CalendarMonthDTO getCalendarMonthWithCourseDeadlines(User user, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        // Get all enrolled courses for the user that have target completion dates within this month
        List<EnrolledCourse> enrolledCourses = enrolledCourseRepository.findByUserAndTargetCompletionDateBetween(
            user, startDate, endDate);
        
        // Convert enrolled courses to calendar events
        Map<String, List<CalendarEventDTO>> eventsByDate = new HashMap<>();
        
        for (EnrolledCourse enrolledCourse : enrolledCourses) {
            if (enrolledCourse.getTargetCompletionDate() != null) {
                String dateKey = enrolledCourse.getTargetCompletionDate().toString();
                
                CalendarEventDTO event = new CalendarEventDTO();
                event.setEventDate(enrolledCourse.getTargetCompletionDate());
                event.setTitle(enrolledCourse.getCourse().getTitle());
                event.setDescription("Target completion date for " + enrolledCourse.getCourse().getTitle());
                event.setEventType(enrolledCourse.getStatus());
                event.setEnrolledCourseId(enrolledCourse.getId());
                event.setCourseTitle(enrolledCourse.getCourse().getTitle());
                
                if (!eventsByDate.containsKey(dateKey)) {
                    eventsByDate.put(dateKey, new ArrayList<>());
                }
                eventsByDate.get(dateKey).add(event);
            }
        }
        
        CalendarMonthDTO calendarMonth = new CalendarMonthDTO();
        calendarMonth.setYear(year);
        calendarMonth.setMonth(month);
        calendarMonth.setFirstDay(startDate);
        calendarMonth.setLastDay(endDate);
        calendarMonth.setEvents(eventsByDate);
        
        return calendarMonth;
    }
}