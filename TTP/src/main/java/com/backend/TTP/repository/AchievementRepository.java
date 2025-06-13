package com.backend.TTP.repository;

import com.backend.TTP.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    List<Achievement> findByIsActiveTrue();
    List<Achievement> findByCategory(String category);
    List<Achievement> findByBadgeLevel(String badgeLevel);
}