package com.jkomerce.store.service;

import com.jkomerce.store.domain.OrderStatus;
import com.jkomerce.store.domain.OrderType;
import com.jkomerce.store.dto.*;
import com.jkomerce.store.exception.UnauthorizedException;
import com.jkomerce.store.mapper.CartMapper;
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
    private final CartMapper cartMapper;

    public OrderService(OrderMapper orderMapper, ItemMapper itemMapper, CartMapper cartMapper){
        this.orderMapper = orderMapper;
        this.itemMapper = itemMapper;
        this.cartMapper = cartMapper;
    }

    @Transactional
    public OrderDTO createOrder(OrderCreateRequestDTO req, HttpSession session) {
        Integer userId = getUserIdFromSession(session); // 내 플로젝트 세션 키에 맞춰 조정 가능

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
    public Long createOrderFromCart(HttpSession session){
        //세션에서 UserId
        Integer userId = getUserIdFromSession(session);

        //장바구니 확인
        Long cartId = cartMapper.selectActiveCartIdByUserId(userId);
        //400
        if(cartId == null) throw new IllegalArgumentException("장바구니가 비어있습니다");


        List<CartItemForOrderDTO> items= cartMapper.selectCartItemsForOrder(cartId);
        // 400
        if(items.isEmpty()) throw new IllegalArgumentException("장바구니가 비어있습니다.");


        OrderDTO order = new OrderDTO();
        order.setUserId(userId);
        order.setOrderType(OrderType.CART.toDbValue());
        order.setTotalAmount(0L);
        order.setStatus(OrderStatus.PENDING.toDbValue());;
        orderMapper.insertOrder(order);

        Long orderId = order.getOrderId();
        if(orderId == null) throw new IllegalArgumentException("orderId 생성 실패");

        List<OrderItemDTO> orderItems = new ArrayList<>();

        Long totalAmount = 0L;
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

    private Integer getUserIdFromSession(HttpSession session) {
        Object v = session.getAttribute("userId");
        if(v instanceof Integer) return (Integer) v;

        // 프로젝트마다 세션에 UserDTO를 넣는 경우도 있어서 fallback
        Object userObj = session.getAttribute("user");
        if (userObj instanceof UserDTO) return ((UserDTO) userObj).getId();

        throw new UnauthorizedException("로그인이 필요합니다.");
    }
}
