package com.rdutta.ddd.ordering.application.port.inbound;

import java.util.List;

public record CreateOrderCommand(
        List<CreateOrderItemCommand> items
) {
}