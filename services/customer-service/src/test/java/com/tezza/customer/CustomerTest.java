package com.tezza.customer;

import com.tezza.customer.dto.CustomerRequest;
import com.tezza.customer.model.Customer;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerTest {
    @Test
    void reservingCreditReducesAvailableLimit() {
        Customer customer = new Customer(new CustomerRequest("Ada", "Lovelace", "ada@example.com", "555", new BigDecimal("1000.00")));

        customer.reserve(new BigDecimal("250.00"));

        assertEquals(new BigDecimal("750.00"), customer.getAvailableLimit());
    }
}