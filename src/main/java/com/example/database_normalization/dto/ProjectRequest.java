package com.example.database_normalization.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProjectRequest(
    @NotBlank(message = "name is required")
    String name,

    @NotNull(message = "budget is required")
    @DecimalMin(message = "budget cannot be negative", value = "0.0")
    BigDecimal budget,

    @NotNull(message = "teamId is required")
    Long teamId
) {}
