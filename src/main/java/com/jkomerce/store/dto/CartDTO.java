package com.jkomerce.store.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CartDTO {

    private Long cartId;
    private Long userId;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private LocalDateTime deleteAt;

}
