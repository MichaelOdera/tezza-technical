package com.tezza.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateProductDto(@NotBlank String code,
                               @NotBlank String name,
                               @Min(1) int tenureValue,
                               @NotBlank String tenureUnit,
                               @NotBlank String serviceFeeType,
                               @NotNull @DecimalMin("0.00") BigDecimal serviceFeeAmount,
                               @NotNull @DecimalMin("0.00") BigDecimal dailyFee,
                               @NotNull @DecimalMin("0.00") BigDecimal lateFeeAmount,
                               @Min(0) int lateFeeTriggerDays)
{ }
