package com.jkomerce.store.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private Integer seq;
    private Integer orderId;
    private Integer itemId;
    private Integer quantity;
    private Integer unitPrice;
    private Integer lineAmount;
}
