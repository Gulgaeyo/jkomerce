package com.jkomerce.store.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailResponseDTO {
    private Long orderId;
    private Integer userId;
    private String orderType;
    private Long totalAmount;
    private String status;
    private LocalDateTime createAt;
    private List<OrderItemDTO> items;
}
