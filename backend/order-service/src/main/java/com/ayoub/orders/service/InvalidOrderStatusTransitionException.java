package com.ayoub.orders.service;

import com.ayoub.orders.domain.OrderStatus;
import java.util.UUID;

public class InvalidOrderStatusTransitionException extends RuntimeException {
    private final UUID orderId;
    private final OrderStatus from;
    private final OrderStatus to;

    public InvalidOrderStatusTransitionException(UUID orderId, OrderStatus from, OrderStatus to) {
        super("Invalid transition for order " + orderId + ": " + from + " -> " + to);
        this.orderId = orderId;
        this.from = from;
        this.to = to;
    }

    public UUID getOrderId() { return orderId; }
    public OrderStatus getFrom() { return from; }
    public OrderStatus getTo() { return to; }
}
