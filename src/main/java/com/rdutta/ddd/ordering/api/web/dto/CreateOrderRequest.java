package com.rdutta.ddd.ordering.api.web.dto;

import java.util.List;

public record CreateOrderRequest(
        List<OrderItemRequest> items
) {
}
