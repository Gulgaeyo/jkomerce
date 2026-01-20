package com.jkomerce.store.controller;

import com.jkomerce.store.dto.OrderCreateRequestDTO;
import com.jkomerce.store.dto.OrderDTO;
import com.jkomerce.store.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody OrderCreateRequestDTO req,
                                    HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        OrderDTO order = orderService.createOrder(req, session);
        return ResponseEntity.status(201).body(order);
    }
}
