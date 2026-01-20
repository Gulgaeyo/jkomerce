package com.jkomerce.store.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderCreateRequestDTO {
    private String orderType;
    private List<OrderItemRequestDTO> orderItems;
}
