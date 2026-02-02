package com.jkomerce.store.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CartItemDTO {

    private Long seq;
    private Long cartId;
    private Long itemId;
    private Integer quantity;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private LocalDateTime deleteAt;
}
