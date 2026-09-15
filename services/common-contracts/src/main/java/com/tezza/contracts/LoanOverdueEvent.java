package com.tezza.contracts;

import java.util.UUID;

public record LoanOverdueEvent(UUID loanId, UUID customerId, String eventType) {
}