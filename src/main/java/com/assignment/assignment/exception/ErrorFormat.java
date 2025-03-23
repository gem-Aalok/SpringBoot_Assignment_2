package com.assignment.assignment.exception;

import java.time.LocalDateTime;

public class ErrorFormat {
    private LocalDateTime timestamp;
    private String message;
    private String details;

    public ErrorFormat(LocalDateTime timestamp , String message , String details){
        super();
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }
}
