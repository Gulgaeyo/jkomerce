package com.jkomerce.store.exception;

public class OutOfStockException extends RuntimeException {
    private final String failReason;

    public OutOfStockException(String message, String failReason) {
        super(message);
        this.failReason = failReason;
    }

    public String getFailReason() {
        return failReason;
    }
}
