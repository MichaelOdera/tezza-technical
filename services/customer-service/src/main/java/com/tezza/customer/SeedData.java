package com.tezza.customer.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;
import com.tezza.customer.dto.CustomerRequest;
import com.tezza.customer.model.Customer;
import com.tezza.customer.repository.CustomerRepository;

@Configuration
public class SeedData {
    @Bean CommandLineRunner seedCustomers(CustomerRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Customer(new CustomerRequest("Ada", "Lovelace", "ada@example.com", "+10000000001", new BigDecimal("10000.00"))));
                repository.save(new Customer(new CustomerRequest("Grace", "Hopper", "grace@example.com", "+10000000002", new BigDecimal("25000.00"))));
            }
        };
    }
}