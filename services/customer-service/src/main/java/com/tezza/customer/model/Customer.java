package com.tezza.customer.model;

import com.tezza.customer.dto.CustomerRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class Customer {
    @Id
    @GeneratedValue
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private BigDecimal loanLimit;
    private BigDecimal availableLimit;

    protected Customer() {
    }

    public Customer(CustomerRequest request) {
        firstName = request.firstName();
        lastName = request.lastName();
        email = request.email();
        phone = request.phone();
        loanLimit = request.loanLimit();
        availableLimit = request.loanLimit();
    }

    public UUID getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public BigDecimal getLoanLimit() { return loanLimit; }
    public BigDecimal getAvailableLimit() { return availableLimit; }

    public void reserve(BigDecimal amount) {
        availableLimit = availableLimit.subtract(amount);
    }
}
