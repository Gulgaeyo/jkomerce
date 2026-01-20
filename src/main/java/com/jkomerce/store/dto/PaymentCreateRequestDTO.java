package com.jkomerce.store.dto;

import lombok.Data;

@Data
public class PaymentCreateRequestDTO {
    private Long orderId;
    private String method;
    private String provider;
    private String idempotencyKey;
}
