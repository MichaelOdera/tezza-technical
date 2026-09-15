package com.tezza.contracts;

import java.math.BigDecimal;
import java.util.UUID;

public record RepaymentReceivedEvent(UUID loanId, UUID customerId, BigDecimal amount, String eventType) {
}