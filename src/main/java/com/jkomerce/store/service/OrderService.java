package com.jkomerce.store.service;

import com.jkomerce.store.auth.SessionUserProvider;
import com.jkomerce.store.domain.OrderStatus;
import com.jkomerce.store.domain.OrderType;
import com.jkomerce.store.dto.*;
import com.jkomerce.store.mapper.CartMapper;
import com.jkomerce.store.mapper.ItemMapper;
import com.jkomerce.store.mapper.OrderMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final ItemMapper itemMapper;
    private final CartMapper cartMapper;
    private final SessionUserProvider sessionUserProvider;

    @Transactional
    public OrderDTO createOrder(OrderCreateRequestDTO req, HttpSession session) {
        Long userId = sessionUserProvider.getRequiredUserId(session); // 내 플로젝트 세션 키에 맞춰 조정 가능

        //1) 총액 계산 + order_items 만들기
        Long totalAmount = 0L;
        List<OrderItemDTO> orderItems = new ArrayList<>();

        for (OrderItemRequestDTO r : req.getOrderItems()) {

            ItemDTO item = itemMapper.selectItemById(r.getItemId());
            if (item == null) {
                // 400
                throw new IllegalArgumentException("상품이 존재하지 않습니다. itemId=" + r.getItemId());
            }
            if (r.getQuantity() <= 0) {  // r,getQuantity() == null ||  <-- int타입이라서 not null?
                throw new IllegalArgumentException("수량이 올바르지 않습니다. quantity=" + r.getQuantity());
            }

//            // 재고 체크 -> 재고 선점 개념으로 삭제
//            int stock = item.getStock();
//            if (stock < r.getQuantity()){
//                throw new IllegalArgumentException("재고 부족. stock=" + stock);
//            }

            // 집계
            Long unitPrice = item.getPrice();
            Long lineAmount = unitPrice * r.getQuantity();
            totalAmount += lineAmount;

            // OrderItems 생성
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
        order.setStatus(OrderStatus.PENDING.toDbValue());

        orderMapper.insertOrder(order);

        // 3) order_items insert N개
        for (OrderItemDTO oi : orderItems) {
            oi.setOrderId(order.getOrderId());
            orderMapper.insertOrderItem(oi);
        }

        return order;
    }

    @Transactional
    public Long createOrderFromCart(HttpSession session, String idempotencyKey){
        // 세션에서 UserId
        Long userId = sessionUserProvider.getRequiredUserId(session);

        // idempotencyKey check (400)
        if(idempotencyKey == null || idempotencyKey.isBlank()){
            throw new IllegalArgumentException("idempotencyKey가 필요합니다.");
        }

        // 기존 주문 확인 -> 존재 시 기존 주문
        OrderDTO existing = orderMapper.selectOrderByIdempotencyKey(idempotencyKey, userId);
        if(existing != null){return existing.getOrderId();}

        // 장바구니 존재 확인 (400)
        Long cartId = cartMapper.selectActiveCartIdByUserId(userId);
        if(cartId == null) throw new IllegalArgumentException("장바구니가 비어있습니다");


        // 장바구니 목록 확인 (400)
        List<CartItemForOrderDTO> items= cartMapper.selectCartItemsForOrder(cartId);
        if(items.isEmpty()) throw new IllegalArgumentException("장바구니가 비어있습니다.");


        OrderDTO order = new OrderDTO();
        order.setUserId(userId);
        order.setOrderType(OrderType.CART.toDbValue());
        order.setTotalAmount(0L);
        order.setStatus(OrderStatus.PENDING.toDbValue());;
        order.setIdempotencyKey(idempotencyKey);

        try {
            orderMapper.insertOrder(order);
        } catch (Exception e){
            // 유니크 충돌 시 기존 주문 반환
            OrderDTO raced = orderMapper.selectOrderByIdempotencyKey(idempotencyKey, userId);
            if(raced != null) return raced.getOrderId();
            throw e;
        }


        Long orderId = order.getOrderId();
        if(orderId == null) throw new IllegalArgumentException("orderId 생성 실패");


        Long totalAmount = 0L;
        List<OrderItemDTO> orderItems = new ArrayList<>();

        for(CartItemForOrderDTO item : items){
            Long itemId = item.getItemId();
            Integer quantity = item.getQuantity();
            if(itemId == null) throw new IllegalArgumentException("itemId가 비어있습니다.");
            if(quantity == null || quantity <= 0) throw new IllegalArgumentException("수량이 올바르지 않습니다.");

            ItemDTO itemDTO = itemMapper.selectItemById(itemId);
            if(itemDTO == null) throw new IllegalArgumentException("상품이 존재하지 않습니다.");

            //orderItem에 담기
            Long unitPrice = itemDTO.getPrice();
            Long lineAmount = unitPrice * quantity;

            OrderItemDTO oi = new OrderItemDTO();
            oi.setItemId(itemId);
            oi.setQuantity(quantity);
            oi.setOrderId(order.getOrderId());
            oi.setUnitPrice(unitPrice);
            oi.setLineAmount(lineAmount);

            orderItems.add(oi);
            totalAmount += lineAmount;
        }

        // 안정성
        orderMapper.updateOrderTotalAmount(orderId, totalAmount);

        for (OrderItemDTO oi : orderItems) {
            orderMapper.insertOrderItem(oi);
        }

        return orderId;
    }
}
