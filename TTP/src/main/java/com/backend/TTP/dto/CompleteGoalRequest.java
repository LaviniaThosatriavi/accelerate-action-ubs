package com.backend.TTP.dto;

import lombok.Data;

import java.util.List;

@Data
public class CompleteGoalRequest {
    private List<Long> completedGoalIds;
}