package com.aquatracker.common;

import java.util.Map;

public class ErrorResponseDto {
    private String error;
    private String message;
    private String field;
    private Map<String, Object> details;

    public ErrorResponseDto() {}

    public ErrorResponseDto(String error, String message) {
        this.error = error;
        this.message = message;
    }

    public ErrorResponseDto(String error, String message, String field) {
        this.error = error;
        this.message = message;
        this.field = field;
    }

    public ErrorResponseDto(String error, String message, String field, Map<String, Object> details) {
        this.error = error;
        this.message = message;
        this.field = field;
        this.details = details;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
}

