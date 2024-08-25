package com.clothing.management.models;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String message;
    private String details;
    private LocalDateTime timestamp;
    private int status;

    public ErrorResponse(String message, String details, int status) {
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }
}