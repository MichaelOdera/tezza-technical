package com.tezza.contracts;

import java.util.UUID;

public record CustomerCreatedEvent(UUID customerId, String email, String phone) {
}