package com.tezza.loan.repository;

import com.tezza.loan.model.Installment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InstallmentRepository extends JpaRepository<Installment, UUID> {
    List<Installment> findByLoanIdOrderByInstallmentNumber(UUID loanId);
}
