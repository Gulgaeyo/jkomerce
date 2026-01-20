package com.jkomerce.store.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Long paymentId;
    private Long orderId;

    private Long amount;
    private String status;

    private String method;
    private String provider;
    private String idempotencyKey;

    private LocalDateTime expiresAt;
    private LocalDateTime approvedAt;
    private String failReason;
    private String pgTid;

    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private LocalDateTime deleteAt;
}
