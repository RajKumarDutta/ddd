package com.rdutta.ddd.ordering.application.port.outbound;

import com.rdutta.ddd.ordering.domain.model.Order;
import com.rdutta.ddd.ordering.domain.valueobject.OrderId;

import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(OrderId orderId);
}
