package com.example.database_normalization.dto;

import jakarta.validation.constraints.NotNull;

public record AddTeamMemberRequest(

    @NotNull(message = "userId is required")
    Long userId

) {}
