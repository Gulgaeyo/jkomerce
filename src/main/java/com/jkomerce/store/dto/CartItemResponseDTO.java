package com.jkomerce.store.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CartItemResponseDTO {

    private Long itemId;
    private Integer quantity;
    //join으로 채우기
    private String itemName;
    private Long price;

}
