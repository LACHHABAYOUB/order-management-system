package com.ayoub.orders.repo;

import com.ayoub.orders.domain.OrderEntity;
import com.ayoub.orders.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
    Page<OrderEntity> findAllByStatus(OrderStatus status, Pageable pageable);
}

