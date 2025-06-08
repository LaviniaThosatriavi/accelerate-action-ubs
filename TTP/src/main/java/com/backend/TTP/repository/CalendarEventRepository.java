package com.backend.TTP.repository;

import com.backend.TTP.model.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {
    List<CalendarEvent> findByUserIdAndEventDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT e FROM CalendarEvent e WHERE e.user.id = :userId AND YEAR(e.eventDate) = :year AND MONTH(e.eventDate) = :month")
    List<CalendarEvent> findByUserIdAndYearAndMonth(Long userId, int year, int month);
}