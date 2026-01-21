package com.ayoub.orders.error;

import java.time.Instant;
import java.util.Map;

public class ApiError {

    private Instant timestamp;
    private String path;
    private String error;
    private String message;
    private Map<String, Object> details;

    public ApiError(Instant timestamp, String path, String error, String message, Map<String, Object> details) {
        this.timestamp = timestamp;
        this.path = path;
        this.error = error;
        this.message = message;
        this.details = details;
    }

    public Instant getTimestamp() { return timestamp; }
    public String getPath() { return path; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public Map<String, Object> getDetails() { return details; }
}
