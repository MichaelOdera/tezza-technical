package com.tezza.loan.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Installment {
    @Id
    @GeneratedValue
    private UUID id;
    private UUID loanId;
    private int installmentNumber;
    private BigDecimal amountDue;
    private BigDecimal amountPaid;
    private LocalDate dueDate;
    private String state;

    protected Installment() {
    }

    public Installment(UUID loanId, int number, BigDecimal amount, LocalDate dueDate) {
        this.loanId = loanId;
        installmentNumber = number;
        amountDue = amount;
        amountPaid = BigDecimal.ZERO;
        this.dueDate = dueDate;
        state = "PENDING";
    }

    public BigDecimal remaining() { return amountDue.subtract(amountPaid).max(BigDecimal.ZERO); }

    public void apply(BigDecimal amount) {
        amountPaid = amountPaid.add(amount).min(amountDue);
        if (amountPaid.compareTo(amountDue) == 0) state = "PAID";
    }

    public int getInstallmentNumber() { return installmentNumber; }
    public UUID getLoanId() { return loanId; }
    public BigDecimal getAmountDue() { return amountDue; }
    public BigDecimal getAmountPaid() { return amountPaid; }
    public LocalDate getDueDate() { return dueDate; }
    public String getState() { return state; }
}
