package com.rdutta.ddd.ordering.application.port.inbound;

import com.rdutta.ddd.ordering.domain.valueobject.OrderId;

public interface CheckoutOrderUseCase {
    OrderId checkout(CreateOrderCommand command, String idempotencyKey);
}
