package com.jkomerce.store.dto;

import lombok.Data;

@Data
public class OrderItemRequestDTO {
    private int itemId;
    private int quantity;

}
