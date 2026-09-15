package com.tezza.loan.repository;

import com.tezza.loan.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {
    List<Loan> findByStateAndDueDateBefore(String state, LocalDate date);
    java.util.Optional<Loan> findByIdempotencyKey(String idempotencyKey);
}
