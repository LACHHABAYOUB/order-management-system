package com.ayoub.orders.repo;

import com.ayoub.orders.domain.OrderAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderAuditRepository extends JpaRepository<OrderAuditEntity, Long> {
    List<OrderAuditEntity> findByOrderIdOrderByCreatedAtDesc(UUID orderId);
}
