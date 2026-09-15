package com.tezza.product.dto;

import com.tezza.product.model.Product;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductDto(UUID id, String code, String name, int tenureValue, String tenureUnit,
                         String serviceFeeType, BigDecimal serviceFeeAmount, BigDecimal dailyFee,
                         BigDecimal lateFeeAmount, int lateFeeTriggerDays, boolean active) {
    public static ProductDto from(Product product) {
        return new ProductDto(product.getId(), product.getCode(), product.getName(), product.getTenureValue(), product.getTenureUnit(),
                product.getServiceFeeType(), product.getServiceFeeAmount(), product.getDailyFee(), product.getLateFeeAmount(),
                product.getLateFeeTriggerDays(), product.isActive());
    }
}
