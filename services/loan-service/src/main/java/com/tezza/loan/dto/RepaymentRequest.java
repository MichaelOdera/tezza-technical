package com.tezza.loan.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RepaymentRequest(@NotNull @DecimalMin("0.01") BigDecimal amount) {
}
