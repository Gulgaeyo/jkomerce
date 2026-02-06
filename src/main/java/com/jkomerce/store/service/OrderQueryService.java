package com.jkomerce.store.service;

import com.jkomerce.store.domain.OrderStatus;
import com.jkomerce.store.domain.OrderType;
import com.jkomerce.store.dto.OrderDTO;
import com.jkomerce.store.dto.OrderDetailResponseDTO;
import com.jkomerce.store.dto.OrderItemDTO;
import com.jkomerce.store.mapper.OrderMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderQueryService {

    private final OrderMapper orderMapper;

    public OrderDetailResponseDTO getOrderDetail(Long orderId, HttpSession session) {
        Integer userId = getUserIdFromSession(session);

        // 1) 내 주문인지까지 포함해서 조회
        OrderDTO order = orderMapper.selectOrderByIdAndUserId(orderId, userId);
        if (order == null) {
            //존재 숨기기
            throw new IllegalArgumentException("주문을 찾을 수 없습니다.");
        }

        // 2) 아이템 조회
        List<OrderItemDTO> items = orderMapper.selectOrderItemsByOrderId(orderId);

        // 3) 조립
        OrderDetailResponseDTO res = new OrderDetailResponseDTO();
        OrderType orderType = OrderType.fromRequired(order.getOrderType());
        res.setOrderId(order.getOrderId());
        res.setUserId(order.getUserId());
        res.setOrderType(orderType.toDbValue());
        res.setTotalAmount(order.getTotalAmount());
        res.setStatus(order.getStatus());
        res.setCreateAt(order.getCreateAt());
        res.setItems(items);

        return res;
    }

    public List<OrderDTO> getMyOrders(Integer page, Integer size, String status, HttpSession session ){
        Integer userId = getUserIdFromSession(session);

        int p = (page == null || page < 1) ? 1 : page;
        int s = (size == null || size < 1) ? 20 : Math.min(size, 100);
        int offset = (p - 1) * s;

        // status enum 활용 검즘/ 정규화
        OrderStatus st = OrderStatus.fromNullableParam(status);
        String normalizedStatus = (st == null) ? null : st.toDbValue();


        return orderMapper.selectOrdersByUserId(userId, s, offset, normalizedStatus);
    }

    private Integer getUserIdFromSession(HttpSession session) {
        if(session == null) throw new IllegalStateException("세션이 없습니다.");
        Object v = session.getAttribute("userId");
        if(v == null) throw new IllegalStateException("로그인이 필요합니다.");
        return (Integer) v;
    }
}

