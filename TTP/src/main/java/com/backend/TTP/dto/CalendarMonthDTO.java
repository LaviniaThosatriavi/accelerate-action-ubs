package com.backend.TTP.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "Calendar month view containing events and important dates for a specific month")
public class CalendarMonthDTO {
    
    @Schema(description = "Year of the calendar month", example = "2024", minimum = "2020", maximum = "2030")
    private int year;
    
    @Schema(description = "Month number (1-12)", example = "6", minimum = "1", maximum = "12")
    private int month;
    
    @Schema(description = "First day of the month", example = "2024-06-01", format = "date", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate firstDay;
    
    @Schema(description = "Last day of the month", example = "2024-06-30", format = "date", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate lastDay;
    
    @Schema(description = "Map of date strings to list of events for that date", 
            example = "{\"2024-06-15\": [{\"title\": \"Assignment Due\", \"eventType\": \"DEADLINE\"}], \"2024-06-20\": [{\"title\": \"Course Completion\", \"eventType\": \"MILESTONE\"}]}",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Map<String, List<CalendarEventDTO>> events; // Map of date string to events
}