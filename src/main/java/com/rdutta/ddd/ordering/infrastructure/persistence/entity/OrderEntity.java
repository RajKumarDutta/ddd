package com.rdutta.ddd.ordering.infrastructure.persistence.entity;

import com.rdutta.ddd.ordering.domain.valueobject.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
public class OrderEntity {
    @Id
    private UUID id;
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItemEntity> items = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    @Column(name = "total_order_price", nullable = false)
    private BigDecimal totalOrderPrice;
    @Column(name = "order_placed_at", nullable = false)
    private Instant orderPlacedAt;

    protected OrderEntity() {}

    public OrderEntity(UUID id, OrderStatus status, BigDecimal totalOrderPrice, Instant orderPlacedAt) {
        this.id = id;
        this.status = status;
        this.totalOrderPrice = totalOrderPrice;
        this.orderPlacedAt = orderPlacedAt;
    }

    public void addItems(List<OrderItemEntity> items) {
        this.items.addAll(items);
    }
}
