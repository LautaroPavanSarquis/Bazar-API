package com.lautaro.bazar_api.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(Long productId, int availableStock, int requestedQuantity) {
        super("Insufficient stock for productId=" + productId +
                ". Available=" + availableStock +
                ", requested=" + requestedQuantity);
    }
}

