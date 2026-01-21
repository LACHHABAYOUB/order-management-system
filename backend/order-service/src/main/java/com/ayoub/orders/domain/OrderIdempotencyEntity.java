package com.ayoub.orders.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "order_idempotency")
public class OrderIdempotencyEntity {

    @Id
    @Column(name="idempotency_key", nullable=false, length = 80)
    private String idempotencyKey;

    @Column(name="request_hash", nullable=false, length = 64)
    private String requestHash;

    @Column(name="order_id", nullable=false)
    private UUID orderId;

    @Column(name="created_at", nullable=false)
    private Instant createdAt;

    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }

    public String getRequestHash() { return requestHash; }
    public void setRequestHash(String requestHash) { this.requestHash = requestHash; }

    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
