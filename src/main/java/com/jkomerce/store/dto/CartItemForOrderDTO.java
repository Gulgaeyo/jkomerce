package com.jkomerce.store.dto;

import lombok.Data;

@Data
public class CartItemForOrderDTO {
    private Long itemId;
    private Integer quantity;
}
