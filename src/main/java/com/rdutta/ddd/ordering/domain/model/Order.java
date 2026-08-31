package com.rdutta.ddd.ordering.domain.model;

import com.rdutta.ddd.ordering.domain.valueobject.OrderId;
import com.rdutta.ddd.ordering.domain.valueobject.OrderStatus;
import com.rdutta.ddd.shared.valueobject.Money;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class Order {
    private final OrderId orderId;
    private final List<OrderItem> orderItems;
    private Money totalOrderPrice;
    private OrderStatus orderStatus;
    private final Instant orderPlacedAt;

    public Order(List<OrderItem> orderItems) {
        Objects.requireNonNull(
                orderItems,
                "Order items cannot be null"
        );

        if (orderItems.isEmpty()) {
            throw new IllegalArgumentException(
                    "Order must contain at least one item"
            );
        }
        this.orderId = new OrderId();
        this.orderItems = new ArrayList<>(orderItems);
        this.orderStatus = OrderStatus.ORDER_CREATED;
        this.orderPlacedAt = Instant.now();
        calculateTotalOrderPrice();
    }

    public Order(
            OrderId orderId,
            List<OrderItem> orderItems,
            Money totalOrderPrice,
            OrderStatus orderStatus,
            Instant orderPlacedAt
    ) {
        this.orderId = Objects.requireNonNull(orderId);
        this.orderItems = new ArrayList<>(orderItems);
        this.totalOrderPrice = Objects.requireNonNull(totalOrderPrice);
        this.orderStatus = Objects.requireNonNull(orderStatus);
        this.orderPlacedAt = Objects.requireNonNull(orderPlacedAt);
    }

    public void accepted(){
        if(this.orderStatus != OrderStatus.ORDER_CREATED) {
            throw new IllegalStateException("Order is not created!");
        }
        this.orderStatus = OrderStatus.ORDER_ACCEPTED;
    }

    public void confirmed(){
        if(this.orderStatus != OrderStatus.ORDER_ACCEPTED) {
            throw new IllegalStateException("Order can't be confirmed!");
        }
        this.orderStatus = OrderStatus.ORDER_CONFIRMED;
    }

    public void cancelled(){
        if(this.orderStatus != OrderStatus.ORDER_CONFIRMED) {
            throw new IllegalStateException("Order can't be cancelled!");
        }
        this.orderStatus = OrderStatus.ORDER_CANCELLED;
    }

    public void calculateTotalOrderPrice() {
        this.totalOrderPrice = orderItems.stream()
                .map(OrderItem::calculateSubtotal)
                .reduce(Money.ZERO, Money::add);
    }
}
