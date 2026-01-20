package com.jkomerce.store.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ItemDTO {

    private int itemId;
    private String itemName;
    private int stock;
    private int price;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private LocalDateTime deleteAt;


}
