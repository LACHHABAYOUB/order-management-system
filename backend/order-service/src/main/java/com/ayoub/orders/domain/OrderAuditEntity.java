package com.ayoub.orders.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "order_audit")
public class OrderAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="order_id", nullable=false)
    private UUID orderId;

    @Column(name="event_type", nullable=false, length = 40)
    private String eventType;

    @Column(name="from_status", length = 30)
    private String fromStatus;

    @Column(name="to_status", length = 30)
    private String toStatus;

    @Column(name="note", length = 250)
    private String note;

    @Column(name="created_at", nullable=false)
    private Instant createdAt;
    @Column(name="changed_by", nullable=false)
    private Instant getChangedBy;

    public Long getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getFromStatus() { return fromStatus; }
    public void setFromStatus(String fromStatus) { this.fromStatus = fromStatus; }

    public String getToStatus() { return toStatus; }
    public void setToStatus(String toStatus) { this.toStatus = toStatus; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getChangedBy() { return getChangedBy; }
    public void setChangedBy(Instant changedBy) { this.createdAt = changedBy; }
 
}
