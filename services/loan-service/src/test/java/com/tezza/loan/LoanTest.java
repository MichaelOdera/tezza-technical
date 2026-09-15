package com.tezza.loan;

import com.tezza.loan.dto.LoanRequest;
import com.tezza.loan.model.Loan;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoanTest {
    @Test
    void repaymentClosesLoanAtZeroBalance() {
        Loan loan = new Loan(new LoanRequest(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("100.00"), "LUMP_SUM", 1, "test-key"),
                LocalDate.now().plusDays(30), new BigDecimal("10.00"), new BigDecimal("25.00"), 3);

        loan.repay(new BigDecimal("110.00"));

        assertEquals("CLOSED", loan.getState());
        assertEquals(0, loan.getOutstandingBalance().compareTo(BigDecimal.ZERO));
    }
}