package com.jkomerce.store.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class CartOrderCreateRequestDTO {

    @NotBlank(message = "idempotencyKey는 필수입니다.")
    private String idempotencyKey;
}
