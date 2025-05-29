package com.backend.TTP.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProfileRequest {
    private String careerStage;
    private List<String> skills;  // Changed from String to List<String>
    private String goals;
    private Integer hoursPerWeek;
}