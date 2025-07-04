package com.backend.TTP.repository;

import com.backend.TTP.model.Course;
import com.backend.TTP.model.EnrolledCourse;
import com.backend.TTP.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrolledCourseRepository extends JpaRepository<EnrolledCourse, Long> {
    List<EnrolledCourse> findByUser(User user);
    List<EnrolledCourse> findByUserAndStatus(User user, String status);
    Optional<EnrolledCourse> findByUserAndCourse(User user, Course course);
    List<EnrolledCourse> findByUserAndTargetCompletionDateBetween(User user, LocalDate startDate, LocalDate endDate);
    Long countByUserAndStatus(User user, String status);
    
    @Query("SELECT ec FROM EnrolledCourse ec WHERE ec.user = :user AND ec.course.id = :courseId")
    Optional<EnrolledCourse> findByUserAndCourseId(@Param("user") User user, @Param("courseId") Long courseId);
}