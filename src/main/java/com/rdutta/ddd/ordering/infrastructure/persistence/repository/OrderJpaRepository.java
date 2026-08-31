package com.rdutta.ddd.ordering.infrastructure.persistence.repository;

import com.rdutta.ddd.ordering.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
}
