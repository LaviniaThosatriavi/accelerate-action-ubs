package com.backend.TTP.repository;

import com.backend.TTP.model.CourseScore;
import com.backend.TTP.model.EnrolledCourse;
import com.backend.TTP.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseScoreRepository extends JpaRepository<CourseScore, Long> {
    List<CourseScore> findByUser(User user);
    Optional<CourseScore> findByUserAndEnrolledCourse(User user, EnrolledCourse enrolledCourse);
    
    @Query("SELECT AVG(cs.score) FROM CourseScore cs WHERE cs.user = :user")
    Double findAverageScoreByUser(User user);
    
    @Query("SELECT COUNT(cs) FROM CourseScore cs WHERE cs.user = :user")
    Long countByUser(User user);
}