package com.tezza.loan.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateLoanDto(@NotNull UUID customerId, @NotNull UUID productId, @NotNull @DecimalMin("0.01") BigDecimal principal,
                            @NotBlank String structure, @Min(1) int installmentCount,
                            @NotBlank String idempotencyKey) { }
