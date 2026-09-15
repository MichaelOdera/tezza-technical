package com.tezza.loan.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BillingDateRequest(@NotNull LocalDate dueDate) {
}
