package com.rdutta.ddd.ordering.infrastructure.persistence.mapper;

import com.rdutta.ddd.ordering.domain.model.Order;
import com.rdutta.ddd.ordering.domain.model.OrderItem;
import com.rdutta.ddd.ordering.domain.valueobject.OrderId;
import com.rdutta.ddd.ordering.infrastructure.persistence.entity.OrderEntity;
import com.rdutta.ddd.ordering.infrastructure.persistence.entity.OrderItemEntity;
import com.rdutta.ddd.shared.valueobject.Money;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class OrderPersistenceMapper {

    public OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity(
                order.getOrderId().orderId(),
                order.getOrderStatus(),
                order.getTotalOrderPrice().amount(),
                order.getOrderPlacedAt()
        );

        List<OrderItemEntity> itemEntities =
                order.getOrderItems()
                        .stream()
                        .map(item -> toEntity(item, entity))
                        .toList();

        entity.addItems(itemEntities);
        return entity;
    }

    private OrderItemEntity toEntity(
            OrderItem item,
            OrderEntity orderEntity
    ) {

        return new OrderItemEntity(
                UUID.randomUUID(),
                item.getProductName(),
                item.getUnitPrice().amount(),
                item.getQuantity(),
                orderEntity
        );
    }

    public Order toDomain(OrderEntity orderEntity) {

        List<OrderItem> orderItems =
                orderEntity.getItems()
                        .stream()
                        .map(this::toDomain)
                        .toList();

        return new Order(
                new OrderId(orderEntity.getId()),
                orderItems,
                new Money(orderEntity.getTotalOrderPrice()),
                orderEntity.getStatus(),
                orderEntity.getOrderPlacedAt()
        );
    }

    private OrderItem toDomain(OrderItemEntity entity) {

        return new OrderItem(
                entity.getProductName(),
                new Money(entity.getUnitPrice()),
                entity.getQuantity()
        );
    }
}