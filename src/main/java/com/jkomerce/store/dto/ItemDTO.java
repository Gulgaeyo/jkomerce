package com.jkomerce.store.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ItemDTO {

    private Long itemId;
    private String itemName;
    private Integer stock;
    private Long price;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private LocalDateTime deleteAt;


}
