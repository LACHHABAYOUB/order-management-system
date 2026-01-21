package com.ayoub.orders.api;

import com.ayoub.orders.domain.OrderEntity;
import java.time.Instant;
import java.util.UUID;

public class OrderResponse {

    private UUID id;
    private long version;
    private String customerName;
    private String status;
    private Instant createdAt;
    private Instant paidAt;
    private Instant cancelledAt;
    private String cancelReason;

    public static OrderResponse from(OrderEntity e) {
        OrderResponse r = new OrderResponse();
        r.setId(e.getId());
        r.setVersion(e.getVersion());
        r.setCustomerName(e.getCustomerName());
        r.setStatus(e.getStatus().name());
        r.setCreatedAt(e.getCreatedAt());
        r.setPaidAt(e.getPaidAt());
        r.setCancelledAt(e.getCancelledAt());
        r.setCancelReason(e.getCancelReason());
        return r;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public long getVersion() { return version; }
    public void setVersion(long version) { this.version = version; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getPaidAt() { return paidAt; }
    public void setPaidAt(Instant paidAt) { this.paidAt = paidAt; }

    public Instant getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(Instant cancelledAt) { this.cancelledAt = cancelledAt; }

    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }
}
