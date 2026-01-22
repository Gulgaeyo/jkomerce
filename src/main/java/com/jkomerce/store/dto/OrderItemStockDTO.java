package com.jkomerce.store.dto;

import lombok.Data;

@Data
public class OrderItemStockDTO {

    private Long itemId;
    private Integer quantity;
}
