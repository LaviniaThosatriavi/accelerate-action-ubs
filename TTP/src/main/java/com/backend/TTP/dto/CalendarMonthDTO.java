package com.backend.TTP.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class CalendarMonthDTO {
    private int year;
    private int month;
    private LocalDate firstDay;
    private LocalDate lastDay;
    private Map<String, List<CalendarEventDTO>> events; // Map of date string to events
}