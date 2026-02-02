package com.jkomerce.store.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private Integer seq;
    private Long orderId;
    private Long itemId;
    private Integer quantity;
    private Long unitPrice;
    private Long lineAmount;
}
