package com.ayoub.orders.repo;

import com.ayoub.orders.domain.OrderIdempotencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderIdempotencyRepository extends JpaRepository<OrderIdempotencyEntity, String> {
}
