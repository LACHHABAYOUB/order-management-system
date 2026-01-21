package com.ayoub.orders.api;

import com.ayoub.orders.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateOrderStatusRequest {

    @NotNull(message = "status is required")
    private OrderStatus status;

    private String reason;

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
