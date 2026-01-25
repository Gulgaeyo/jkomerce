package com.jkomerce.store.controller;

import com.jkomerce.store.dto.OrderCreateRequestDTO;
import com.jkomerce.store.dto.OrderDTO;
import com.jkomerce.store.dto.OrderDetailResponseDTO;
import com.jkomerce.store.service.OrderQueryService;
import com.jkomerce.store.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderQueryService orderQueryService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody OrderCreateRequestDTO req,
                                    HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        OrderDTO order = orderService.createOrder(req, session);
        return ResponseEntity.status(201).body(order);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponseDTO> getDetail(@PathVariable Long orderId,
                                                            HttpSession session) {
        return ResponseEntity.ok(orderQueryService.getOrderDetail(orderId, session));
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> myOrders(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String status,
            HttpSession session
    ) {
        return ResponseEntity.ok(orderQueryService.getMyOrders(page, size, status, session));
    }




}
