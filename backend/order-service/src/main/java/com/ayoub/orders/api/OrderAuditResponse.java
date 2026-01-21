package com.ayoub.orders.api;

import com.ayoub.orders.domain.OrderAuditEntity;
import java.time.Instant;

public class OrderAuditResponse {

    private String oldStatus;
    private String newStatus;
    private Instant changedAt;
    private Instant changedBy;
    
public static OrderAuditResponse from(OrderAuditEntity e) {
    OrderAuditResponse r = new OrderAuditResponse();
    r.oldStatus = e.getFromStatus();
    r.newStatus = e.getFromStatus();
    r.changedAt = e.getCreatedAt();
    r.changedBy = e.getChangedBy();
    return r;
}


    public String getOldStatus() { return oldStatus; }
    public String getNewStatus() { return newStatus; }
    public Instant getChangedAt() { return changedAt; }
    public Instant getChangedBy() { return changedBy; }
}
