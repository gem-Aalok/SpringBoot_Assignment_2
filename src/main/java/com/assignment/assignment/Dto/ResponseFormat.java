package com.assignment.assignment.Dto;

import java.time.LocalDateTime;

public class ResponseFormat <T>{
    private LocalDateTime timestamp;
    private String message;
    private T data;

    public ResponseFormat(LocalDateTime timestamp, String message, T data) {
        this.timestamp = timestamp;
        this.message = message;
        this.data = data;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
