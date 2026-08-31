package com.rdutta.ddd.ordering.application.port.inbound;

import java.math.BigDecimal;

public record CreateOrderItemCommand(
        String productName,
        BigDecimal unitPrice,
        int quantity
) {
}