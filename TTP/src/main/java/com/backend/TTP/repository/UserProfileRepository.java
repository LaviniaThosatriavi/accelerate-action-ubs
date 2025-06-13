package com.backend.TTP.repository;

import com.backend.TTP.model.User;
import com.backend.TTP.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUser(User user);
    List<UserProfile> findAll();
}