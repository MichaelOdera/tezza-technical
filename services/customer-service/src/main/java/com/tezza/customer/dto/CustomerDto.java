package com.tezza.customer.dto;

import com.tezza.customer.model.Customer;
import java.math.BigDecimal;
import java.util.UUID;

public record CustomerDto(UUID id, String firstName, String lastName, String email, String phone,
                          BigDecimal loanLimit, BigDecimal availableLimit) {
    public static CustomerDto from(Customer customer) {
        return new CustomerDto(customer.getId(), customer.getFirstName(), customer.getLastName(), customer.getEmail(),
                customer.getPhone(), customer.getLoanLimit(), customer.getAvailableLimit());
    }
}
