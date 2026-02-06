package com.jkomerce.store.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Integer id;
    private String loginId;
    private String password;
    private String userName;
    private LocalDate birth;
    private String email;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private LocalDateTime deleteAt;
}
