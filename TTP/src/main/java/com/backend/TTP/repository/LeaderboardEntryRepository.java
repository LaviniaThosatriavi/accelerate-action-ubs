package com.backend.TTP.repository;

import com.backend.TTP.model.LeaderboardEntry;
import com.backend.TTP.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaderboardEntryRepository extends JpaRepository<LeaderboardEntry, Long> {
    Optional<LeaderboardEntry> findByUserAndDate(User user, LocalDate date);
    
    // Daily leaderboard
    @Query("SELECT le FROM LeaderboardEntry le WHERE le.date = :date ORDER BY le.dailyPoints DESC")
    List<LeaderboardEntry> findDailyLeaderboard(LocalDate date);
    
    // Weekly leaderboard
    @Query("SELECT le FROM LeaderboardEntry le WHERE le.date >= :startDate AND le.date <= :endDate ORDER BY le.weeklyPoints DESC")
    List<LeaderboardEntry> findWeeklyLeaderboard(LocalDate startDate, LocalDate endDate);
    
    // Monthly leaderboard
    @Query("SELECT le FROM LeaderboardEntry le WHERE YEAR(le.date) = :year AND MONTH(le.date) = :month ORDER BY le.monthlyPoints DESC")
    List<LeaderboardEntry> findMonthlyLeaderboard(int year, int month);
    
    // Get user's rank for a specific date
    @Query("SELECT COUNT(le) + 1 FROM LeaderboardEntry le WHERE le.date = :date AND le.dailyPoints > (SELECT le2.dailyPoints FROM LeaderboardEntry le2 WHERE le2.user = :user AND le2.date = :date)")
    Integer findUserDailyRank(User user, LocalDate date);
}