package com.jkomerce.store.dto;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BoardDTO {

    private Long id;
    private Integer userId;
    private String userName;
    private String title;
    private String content;
    private String image;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private LocalDateTime deleteAt;

}
