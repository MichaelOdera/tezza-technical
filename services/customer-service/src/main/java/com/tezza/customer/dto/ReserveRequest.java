package com.tezza.customer.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ReserveRequest(@NotNull @DecimalMin("0.01") BigDecimal amount) { }