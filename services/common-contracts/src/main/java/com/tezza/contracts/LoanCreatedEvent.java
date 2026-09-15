package com.tezza.contracts;

import java.math.BigDecimal;
import java.util.UUID;

public record LoanCreatedEvent(UUID loanId, UUID customerId, BigDecimal principal, String eventType) {
}