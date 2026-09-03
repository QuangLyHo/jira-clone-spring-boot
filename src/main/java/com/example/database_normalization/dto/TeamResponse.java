package com.example.database_normalization.dto;

import com.example.database_normalization.entity.Team;

public record TeamResponse(Long id, String name) {

    public static TeamResponse from(Team team) {
        return new TeamResponse(team.getId(), team.getName());
    }
}
