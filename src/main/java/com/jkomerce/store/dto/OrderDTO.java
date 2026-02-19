package com.jkomerce.store.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderDTO {
    private Long orderId;
    private Long userId;
    private String orderType; // DIRECT / CART
    private Long totalAmount;
    private String status;    // PENDING / PAID
    private String idempotencyKey;
    private LocalDateTime createAt;
}
