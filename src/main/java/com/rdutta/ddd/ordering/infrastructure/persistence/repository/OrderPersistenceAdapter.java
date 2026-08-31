package com.rdutta.ddd.ordering.infrastructure.persistence.repository;

import com.rdutta.ddd.ordering.application.port.outbound.OrderRepository;
import com.rdutta.ddd.ordering.domain.model.Order;
import com.rdutta.ddd.ordering.domain.valueobject.OrderId;
import com.rdutta.ddd.ordering.infrastructure.persistence.entity.OrderEntity;
import com.rdutta.ddd.ordering.infrastructure.persistence.mapper.OrderPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class OrderPersistenceAdapter implements OrderRepository {
    private final OrderJpaRepository orderJpaRepository;
    private final OrderPersistenceMapper orderPersistenceMapper;

    public OrderPersistenceAdapter(OrderJpaRepository orderJpaRepository, OrderPersistenceMapper orderPersistenceMapper) {
        this.orderJpaRepository = orderJpaRepository;
        this.orderPersistenceMapper = orderPersistenceMapper;
    }

    @Override
    public Order save(Order order) {
        OrderEntity orderEntity = orderPersistenceMapper.toEntity(order);

        OrderEntity savedOrderEntity = orderJpaRepository.save(orderEntity);

        return orderPersistenceMapper.toDomain(savedOrderEntity);
    }

    @Override
    public Optional<Order> findById(OrderId orderId) {
        return orderJpaRepository
            .findById(orderId.orderId())
                .map(orderPersistenceMapper::toDomain);
    }
}
