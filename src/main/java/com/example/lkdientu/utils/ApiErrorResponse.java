package com.example.lkdientu.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiErrorResponse {
    private String status;
    private ErrorDetails error;
    private String message;
    private String stack;

    // Getters và Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ErrorDetails getError() {
        return error;
    }

    public void setError(ErrorDetails error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }

    // Lớp lồng bên trong để ánh xạ chi tiết lỗi
    public static class ErrorDetails {
        private int statusCode;
        private String status;
        @JsonProperty("isOperational")
        private boolean isOperational;

        // Getters và Setters
        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public boolean isOperational() {
            return isOperational;
        }

        public void setOperational(boolean isOperational) {
            this.isOperational = isOperational;
        }

    }
}
