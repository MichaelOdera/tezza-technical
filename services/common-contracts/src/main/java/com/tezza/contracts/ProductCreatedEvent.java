package com.tezza.contracts;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductCreatedEvent(UUID productId, String code, String name, BigDecimal serviceFeeAmount) {
}