package com.backend.TTP.repository;

import com.backend.TTP.model.DailyGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyGoalRepository extends JpaRepository<DailyGoal, Long> {

    List<DailyGoal> findByUserIdAndGoalDate(Long userId, LocalDate goalDate);
    
    List<DailyGoal> findByUserIdAndGoalDateAndIsCompleted(Long userId, LocalDate goalDate, Boolean isCompleted);
    
    List<DailyGoal> findByUserIdAndGoalDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(g.allocatedHours) FROM DailyGoal g WHERE g.user.id = :userId AND g.goalDate BETWEEN :startDate AND :endDate")
    Double sumAllocatedHoursByUserIdAndDateRange(
            @Param("userId") Long userId, 
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}