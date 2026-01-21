package com.ayoub.orders.service;

public class IdempotencyConflictException extends RuntimeException {
    private final String key;

    public IdempotencyConflictException(String key) {
        super("Idempotency-Key already used with different payload: " + key);
        this.key = key;
    }

    public String getKey() { return key; }
}
