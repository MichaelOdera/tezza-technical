package com.tezza.loan.dto;

import com.tezza.loan.model.Loan;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record LoanDto(UUID id, UUID customerId, UUID productId, BigDecimal principal, BigDecimal outstandingBalance,
                      String structure, String state, LocalDate dueDate, LocalDate consolidatedDueDate) {
    public static LoanDto from(Loan loan) {
        return new LoanDto(loan.getId(), loan.getCustomerId(), loan.getProductId(), loan.getPrincipal(), loan.getOutstandingBalance(),
                loan.getStructure(), loan.getState(), loan.getDueDate(), loan.getConsolidatedDueDate());
    }
}
