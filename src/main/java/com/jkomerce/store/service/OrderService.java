package com.jkomerce.store.service;

import com.jkomerce.store.dto.*;
import com.jkomerce.store.mapper.ItemMapper;
import com.jkomerce.store.mapper.OrderMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderMapper orderMapper;
    private final ItemMapper itemMapper;

    public OrderService(OrderMapper orderMapper, ItemMapper itemMapper){
        this.orderMapper = orderMapper;
        this.itemMapper = itemMapper;
    }

    @Transactional
    public OrderDTO createOrder(OrderCreateRequestDTO req, HttpSession session) {
        Integer userId = getUserIdFromSession(session); // 내 플로젝트 세션 키에 맞춰 조정 가능

        //1) 총액 계산 + order_items 만들기
        int totalAmount = 0;
        List<OrderItemDTO> orderItems = new ArrayList<>();

        for (OrderItemRequestDTO r : req.getOrderItems()) {
            ItemDTO item = itemMapper.selectItemById(r.getItemId());
            if (item == null) {
                throw new IllegalArgumentException("상품이 존재하지 않습니다. itemId=" + r.getItemId());
            }
            if (r.getQuantity() <= 0) {  // r,getQuantity() == null ||  <-- int타입이라서 not null?
                throw new IllegalArgumentException("수량이 올바르지 않습니다. quantity=" + r.getQuantity());
            }

//            // 재고 체크
//            int stock = item.getStock();
//            if (stock < r.getQuantity()){
//                throw new IllegalArgumentException("재고 부족. stock=" + stock);
//            }

            int unitPrice = item.getPrice();
            int lineAmount = unitPrice * r.getQuantity();
            totalAmount += lineAmount;

            OrderItemDTO oi = new OrderItemDTO();
            oi.setItemId(r.getItemId());
            oi.setQuantity(r.getQuantity());
            oi.setUnitPrice(unitPrice);
            oi.setLineAmount(lineAmount);
            orderItems.add(oi);
        }

        // 2) orders insert (orderId 생성)
        OrderDTO order = new OrderDTO();
        order.setUserId(userId);
        order.setOrderType(req.getOrderType());
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");

        orderMapper.insertOrder(order);

        // 3) order_items insert N개
        for (OrderItemDTO oi : orderItems) {
            oi.setOrderId(order.getOrderId());
            orderMapper.insertOrderItem(oi);
        }

        return order;
    }

    private Integer getUserIdFromSession(HttpSession session) {
        Object v = session.getAttribute("userId");
        if(v instanceof Integer) return (Integer) v;

        // 프로젝트마다 세션에 UserDTO를 넣는 경우도 있어서 fallback
        Object userObj = session.getAttribute("user");
        if (userObj instanceof UserDTO) return ((UserDTO) userObj).getId();

        throw new IllegalArgumentException("로그인이 필요합니다.");
    }
}
