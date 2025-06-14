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
    
    // Daily leaderboard - ORDER BY TOTAL POINTS instead of daily points
    @Query("SELECT le FROM LeaderboardEntry le WHERE le.date = :date ORDER BY le.totalPoints DESC, le.user.username ASC")
    List<LeaderboardEntry> findDailyLeaderboard(LocalDate date);
    
    // Get user's daily rank based on TOTAL POINTS
    @Query("SELECT COUNT(DISTINCT le.user) + 1 FROM LeaderboardEntry le WHERE le.date = :date AND le.totalPoints > " +
           "(SELECT COALESCE(le2.totalPoints, 0) FROM LeaderboardEntry le2 WHERE le2.user = :user AND le2.date = :date)")
    Integer findUserDailyRank(User user, LocalDate date);
}