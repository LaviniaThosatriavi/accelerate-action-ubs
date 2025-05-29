package com.backend.TTP.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProfileResponse {
    private Long id;
    private String careerStage;
    private List<String> skills;  
    private String goals;
    private Integer hoursPerWeek;
    private String learningPath;
}