package com.jkomerce.store.dto;

import lombok.Data;

@Data
public class OrderItemRequestDTO {
    private Long itemId;
    private Integer quantity;

}
