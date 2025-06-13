package com.backend.TTP.repository;

import com.backend.TTP.model.User;
import com.backend.TTP.model.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
    List<UserAchievement> findByUser(User user);
    List<UserAchievement> findByUserOrderByEarnedAtDesc(User user);
}
