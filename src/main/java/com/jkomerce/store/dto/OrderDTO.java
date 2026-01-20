package com.jkomerce.store.dto;

import lombok.Data;

@Data
public class OrderDTO {
    private Integer orderId;
    private Integer userId;
    private String orderType; // DIRECT / CART
    private Integer totalAmount;
    private String status; //PENDING / PAID
}
