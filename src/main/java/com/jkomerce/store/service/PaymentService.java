package com.jkomerce.store.service;

import com.jkomerce.store.dto.OrderDTO;
import com.jkomerce.store.dto.OrderItemStockDTO;
import com.jkomerce.store.dto.PaymentCreateRequestDTO;
import com.jkomerce.store.dto.PaymentDTO;
import com.jkomerce.store.mapper.ItemMapper;
import com.jkomerce.store.mapper.OrderMapper;
import com.jkomerce.store.mapper.PaymentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;
    private final ItemMapper itemMapper;
    private final PaymentFailService paymentFailService;

    public PaymentService(PaymentMapper paymentMapper, OrderMapper orderMapper, ItemMapper itemMapper, PaymentFailService paymentFailService) {
        this.paymentMapper = paymentMapper;
        this.orderMapper = orderMapper;
        this.itemMapper = itemMapper;
        this.paymentFailService = paymentFailService;
    }

    @Transactional
    public PaymentDTO createPayment(PaymentCreateRequestDTO req){
        // 1) idem key 중복방지
        PaymentDTO existing = paymentMapper.selectPaymentByIdempotencyKey(req.getIdempotencyKey());
        if(existing != null){
            return existing;
        }

        //pgTid 과생성 방지
        PaymentDTO active = paymentMapper.selectActiveRequestedByOrderId(req.getOrderId());
        if(active != null){ return active;}

        //2) 주문 조회 및 상태 확인
        OrderDTO order = orderMapper.selectOrderById(req.getOrderId());
        if(order == null) throw new IllegalStateException("주문이 존재하지 않습니다.(orderId is null.)");

        if(!"PENDING".equals(order.getStatus())){
            throw new IllegalStateException("결제 가능한 주문 상태가 아닙니다.(Status != PENDING.)");
        }

        PaymentDTO payment = new PaymentDTO();
        payment.setOrderId(req.getOrderId());
        payment.setAmount(order.getTotalAmount().longValue());
        payment.setStatus("REQUESTED");
        payment.setMethod(req.getMethod());
        payment.setProvider(req.getProvider());
        payment.setIdempotencyKey(req.getIdempotencyKey());
        payment.setExpiresAt(LocalDateTime.now().plusMinutes(10));

        paymentMapper.insertPayment(payment);
        return payment;
    }

    @Transactional
    public PaymentDTO approvePayment(Long paymentId, String pgTid){
        PaymentDTO payment = paymentMapper.selectPaymentById(paymentId);
        //payment 조회
        if(payment == null) throw new IllegalStateException("결제가 존재하지 않습니다.(PaymentId is null.)");

        //멱등성 유지 (Status == PAID 이면 결제 반환)
        if("PAID".equals(payment.getStatus())){
            return payment;
        }

        // REQUESTED 가 아니면 409 던짐
        if(!"REQUESTED".equals(payment.getStatus())){
            throw new IllegalStateException("승인 가능한 결제 상태가 아닙니다.(Status != REQUESTED.)");
        }

        // 결제 시간 만료 시 409
        if(payment.getExpiresAt() != null && payment.getExpiresAt().isBefore(LocalDateTime.now())){
            // 결제 시간 만효 시 처리
            paymentFailService.expirePaymentAndOrder(paymentId, payment.getOrderId());
            throw new IllegalStateException("결제 요청이 만료되었습니다.");
        }

        // order 조회 + PENDING 확인 -------------------------------------------------
        OrderDTO order = orderMapper.selectOrderById(payment.getOrderId());
        if(order == null){
            throw new IllegalArgumentException("주문이 존재하지 않습니다.");
        }

        // 주문이 미미 PAID면 (상태 꼬임 방지용) 결제 최신값 반환
        if ("PAID".equals(order.getStatus())) {
            // payment도 PAID일 가능성이 높지만, 최신으로 다시 읽어서 반환
            PaymentDTO lastest = paymentMapper.selectPaymentById(payment.getPaymentId());
            return lastest != null ? lastest : payment;
        }

        if (!"PENDING".equals(order.getStatus())){
            throw new IllegalStateException("결제 가능한 주문 상태가 아닙니다. status= "+ order.getStatus());
        }
        // ----------------------------------------------------------

        // order_item 조회
        List<OrderItemStockDTO> orderItems = orderMapper.selectOrderItemsForStock(order.getOrderId());
        if(orderItems == null || orderItems.isEmpty()){
            paymentFailService.failPaymentAndCancelOrder(paymentId, order.getOrderId(), "EMPTY_ORDER_ITEMS");
            throw new IllegalStateException("주문 상품이 비어있습니다.");
        }

        // 재고 차감 루프 (하나라도 0이면 실패처리)
        for (OrderItemStockDTO oi : orderItems){
            // 수량 유효성
            if (oi.getQuantity() == null || oi.getQuantity() <= 0) {
                paymentFailService.failPaymentAndCancelOrder(paymentId, order.getOrderId(), "INVALID_QUANTITY");
                throw new IllegalStateException("주문 수량이 올바르지 않습니다.");
            }

            int updated = itemMapper.decreaseStock(oi.getItemId(), oi.getQuantity());
            if (updated == 0){
                // 재고 부족 -> 결제 실패 + 주문 취소
                paymentFailService.failPaymentAndCancelOrder(
                        paymentId,
                        order.getOrderId(),
                        "OUT_OF_STOCK:itemId="+oi.getItemId()
                );
                throw new IllegalStateException("재고가 부족합니다. itemId=" + oi.getItemId());
            }
        }

        // payment -> PAID 업데이트 (경쟁 상황 대비: status='REQUESTED' 조건 권장)
        int paidUpdated = paymentMapper.updatePaymentToPaid(paymentId, pgTid);
        if (paidUpdated == 0){
            PaymentDTO lastest = paymentMapper.selectPaymentById(payment.getPaymentId());
            if(lastest != null && "PAID".equals(lastest.getStatus())){
                return lastest;
            }

            throw new IllegalStateException("결제 승인 처리 실패");
        }

        // order -> PAID 업데이트
        orderMapper.updateOrderStatus(order.getOrderId(), "PAID");

        // payment 재조회 반환
        return paymentMapper.selectPaymentById(paymentId);
    }

    @Transactional
    public PaymentDTO failPayment(Long paymentId, String reason) {
        PaymentDTO payment = paymentMapper.selectPaymentById(paymentId);
        if(payment == null) throw new IllegalStateException("결제가 존재하지 않습니다.");

        if(!"REQUESTED".equals(payment.getStatus())){
            throw new IllegalStateException("실패 처리 가능한 결제 상태가 아닙니다.");
        }

        int updated = paymentMapper.updatePaymentToFailed(paymentId, reason);
        if(updated == 0) throw new IllegalStateException("결제 실패 처리 실패");

        orderMapper.updateOrderStatus(payment.getOrderId(), "CANCELED");

        return paymentMapper.selectPaymentById(paymentId);


    }

}
