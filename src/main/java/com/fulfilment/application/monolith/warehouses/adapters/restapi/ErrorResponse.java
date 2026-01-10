package com.fulfilment.application.monolith.warehouses.adapters.restapi;

public class ErrorResponse {
    private String error;
    private long timestamp;

    public ErrorResponse(String error) {
        this.error = error;
        this.timestamp = System.currentTimeMillis();
    }

    public ErrorResponse(String error, long timestamp) {
        this.error = error;
        this.timestamp = timestamp;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}