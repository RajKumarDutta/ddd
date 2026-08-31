package com.rdutta.ddd.ordering.api.web.dto;

import java.math.BigDecimal;

public record OrderItemRequest(
        String productName,
        BigDecimal unitPrice,
        int quantity
) {}