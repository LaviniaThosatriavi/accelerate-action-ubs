package com.backend.TTP.service;

import com.backend.TTP.model.CourseScore;
import com.backend.TTP.model.EnrolledCourse;
import com.backend.TTP.model.User;
import com.backend.TTP.repository.CourseScoreRepository;
import com.backend.TTP.repository.EnrolledCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseScoreService {
    
    @Autowired
    private CourseScoreRepository courseScoreRepository;
    
    @Autowired
    private EnrolledCourseRepository enrolledCourseRepository;
    
    /**
     * Get course score for a specific course
     */
    public CourseScore getCourseScore(User user, Long enrolledCourseId) {
        EnrolledCourse enrolledCourse = enrolledCourseRepository.findById(enrolledCourseId)
                .orElseThrow(() -> new RuntimeException("Enrolled course not found"));
        
        if (!enrolledCourse.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to view this course score");
        }
        
        return courseScoreRepository.findByUserAndEnrolledCourse(user, enrolledCourse)
                .orElseThrow(() -> new RuntimeException("Course score not found"));
    }
    
    /**
     * Get all course scores for user
     */
    public List<CourseScore> getUserScores(User user) {
        return courseScoreRepository.findByUser(user);
    }
}