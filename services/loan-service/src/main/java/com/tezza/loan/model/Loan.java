package com.tezza.loan.model;

import com.tezza.loan.dto.LoanRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Loan {
    @Id
    @GeneratedValue
    private UUID id;
    private UUID customerId;
    @Column(nullable = false, unique = true, updatable = false)
    private String idempotencyKey;
    private UUID productId;
    private BigDecimal principal;
    private BigDecimal outstandingBalance;
    private String structure;
    private String state;
    private LocalDate dueDate;
    private LocalDate consolidatedDueDate;
    private BigDecimal feesCharged;
    private BigDecimal lateFeeAmount;
    private int lateFeeTriggerDays;

    protected Loan() {
    }

    public Loan(LoanRequest request, LocalDate dueDate, BigDecimal fee,
                BigDecimal lateFeeAmount, int lateFeeTriggerDays) {
        customerId = request.customerId();
        idempotencyKey = request.idempotencyKey();
        productId = request.productId();
        principal = request.principal();
        outstandingBalance = principal.add(fee);
        structure = request.structure();
        state = "OPEN";
        this.dueDate = dueDate;
        feesCharged = fee;
        this.lateFeeAmount = lateFeeAmount;
        this.lateFeeTriggerDays = lateFeeTriggerDays;
    }

    public UUID getId() { return id; }
    public UUID getCustomerId() { return customerId; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public UUID getProductId() { return productId; }
    public BigDecimal getPrincipal() { return principal; }
    public BigDecimal getOutstandingBalance() { return outstandingBalance; }
    public String getState() { return state; }
    public String getStructure() { return structure; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getConsolidatedDueDate() { return consolidatedDueDate; }

    public void setConsolidatedDueDate(LocalDate dueDate) { consolidatedDueDate = dueDate; }
    public BigDecimal getLateFeeAmount() { return lateFeeAmount; }
    public int getLateFeeTriggerDays() { return lateFeeTriggerDays; }

    public void addFee(BigDecimal amount) {
        outstandingBalance = outstandingBalance.add(amount);
        feesCharged = feesCharged.add(amount);
    }

    public void repay(BigDecimal amount) {
        outstandingBalance = outstandingBalance.subtract(amount).max(BigDecimal.ZERO);
        if (outstandingBalance.signum() == 0) state = "CLOSED";
    }

    public void overdue() {
        if ("OPEN".equals(state)) state = "OVERDUE";
    }
}
