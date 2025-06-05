package com.backend.TTP.repository;

import com.backend.TTP.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByTagsContaining(String tag);
    
    @Query("SELECT c FROM Course c WHERE c.skillLevel = ?1")
    List<Course> findBySkillLevel(String skillLevel);
    
    List<Course> findByCategory(String category);
}