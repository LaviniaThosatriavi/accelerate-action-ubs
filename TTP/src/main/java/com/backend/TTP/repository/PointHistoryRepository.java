package com.backend.TTP.repository;

import com.backend.TTP.model.PointHistory;
import com.backend.TTP.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    List<PointHistory> findByUser(User user);
    List<PointHistory> findByUserOrderByEarnedAtDesc(User user);
    List<PointHistory> findByUserAndEarnedAtBetween(User user, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT SUM(ph.pointsEarned) FROM PointHistory ph WHERE ph.user = :user")
    Integer getTotalPointsByUser(User user);
    
    @Query("SELECT SUM(ph.pointsEarned) FROM PointHistory ph WHERE ph.user = :user AND ph.earnedAt >= :startDate")
    Integer getPointsByUserSince(User user, LocalDateTime startDate);
}