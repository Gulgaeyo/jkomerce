package com.jkomerce.store.dto;

import lombok.Data;

@Data
public class CartItemAddRequestDTO {

    private Long itemId;
    private Integer quantity;

}
