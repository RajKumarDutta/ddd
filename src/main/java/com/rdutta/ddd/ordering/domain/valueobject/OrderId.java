package com.rdutta.ddd.ordering.domain.valueobject;

import java.util.UUID;

public record OrderId(
        UUID orderId
) {
    public OrderId() {
        this(UUID.randomUUID());
    }
}
