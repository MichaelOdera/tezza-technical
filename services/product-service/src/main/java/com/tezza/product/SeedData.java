package com.tezza.product.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;
import com.tezza.product.dto.ProductRequest;
import com.tezza.product.model.Product;
import com.tezza.product.repository.ProductRepository;

@Configuration
public class SeedData {
    @Bean CommandLineRunner seedProducts(ProductRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Product(new ProductRequest("FLEX-30", "30 Day Flex Loan", 30, "DAYS", "PERCENTAGE", new BigDecimal("5.00"), BigDecimal.ZERO, new BigDecimal("25.00"), 3)));
                repository.save(new Product(new ProductRequest("TERM-12", "12 Month Term Loan", 12, "MONTHS", "FIXED", new BigDecimal("50.00"), BigDecimal.ZERO, new BigDecimal("15.00"), 5)));
            }
        };
    }
}