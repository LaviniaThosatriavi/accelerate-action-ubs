package com.backend.TTP.service;

import com.backend.TTP.model.CourseScore;
import com.backend.TTP.model.User;
import com.backend.TTP.repository.CourseScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseScoreService {
    
    @Autowired
    private CourseScoreRepository courseScoreRepository;
    
    /**
     * Get course score for a specific course by courseId
     */
    public CourseScore getCourseScore(User user, Long courseId) {
        return courseScoreRepository.findByUserAndCourseId(user, courseId)
                .orElseThrow(() -> new RuntimeException("Course score not found"));
    }
    
    /**
     * Get all course scores for user
     */
    public List<CourseScore> getUserScores(User user) {
        return courseScoreRepository.findByUser(user);
    }
}