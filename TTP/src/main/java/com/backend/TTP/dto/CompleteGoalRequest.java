package com.backend.TTP.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@Schema(description = "Request to mark multiple daily goals as completed")
public class CompleteGoalRequest {
    
    @Schema(description = "List of goal IDs to mark as completed", 
            example = "[1, 2, 3]", 
            required = true)
    @NotNull(message = "Completed goal IDs list cannot be null")
    @NotEmpty(message = "At least one goal ID must be provided")
    private List<Long> completedGoalIds;
}