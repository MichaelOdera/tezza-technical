package com.tezza.product.model;

import com.tezza.product.dto.ProductRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class Product {
    @Id
    @GeneratedValue
    private UUID id;
    private String code;
    private String name;
    private int tenureValue;
    private String tenureUnit;
    private String serviceFeeType;
    private BigDecimal serviceFeeAmount;
    private BigDecimal dailyFee;
    private BigDecimal lateFeeAmount;
    private int lateFeeTriggerDays;
    private boolean active;

    protected Product() {
    }

    public Product(ProductRequest request) {
        code = request.code();
        name = request.name();
        tenureValue = request.tenureValue();
        tenureUnit = request.tenureUnit();
        serviceFeeType = request.serviceFeeType();
        serviceFeeAmount = request.serviceFeeAmount();
        dailyFee = request.dailyFee();
        lateFeeAmount = request.lateFeeAmount();
        lateFeeTriggerDays = request.lateFeeTriggerDays();
        active = true;
    }

    public UUID getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public int getTenureValue() { return tenureValue; }
    public String getTenureUnit() { return tenureUnit; }
    public String getServiceFeeType() { return serviceFeeType; }
    public BigDecimal getServiceFeeAmount() { return serviceFeeAmount; }
    public BigDecimal getDailyFee() { return dailyFee; }
    public BigDecimal getLateFeeAmount() { return lateFeeAmount; }
    public int getLateFeeTriggerDays() { return lateFeeTriggerDays; }
    public boolean isActive() { return active; }
}
