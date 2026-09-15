package com.tezza.product;

import com.tezza.product.dto.ProductRequest;
import com.tezza.product.model.Product;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductTest {
    @Test
    void productKeepsFeeAndTenureConfiguration() {
        Product product = new Product(new ProductRequest("FLEX", "Flex", 30, "DAYS", "PERCENTAGE",
                new BigDecimal("5.00"), BigDecimal.ZERO, new BigDecimal("25.00"), 3));

        assertEquals("DAYS", product.getTenureUnit());
        assertEquals(new BigDecimal("5.00"), product.getServiceFeeAmount());
        assertEquals(3, product.getLateFeeTriggerDays());
    }
}