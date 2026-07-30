package com.example.database_normalization.dto;

import java.math.BigDecimal;

import com.example.database_normalization.entity.Project;

public record ProjectResponse(Long id, String name, BigDecimal budget) {
    
    public static ProjectResponse from(Project project) {
        return new ProjectResponse(project.getId(), project.getName(), project.getBudget());
    }
}
