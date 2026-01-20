package com.jkomerce.store.service;

import com.jkomerce.store.dto.OrderDTO;
import com.jkomerce.store.dto.PaymentCreateRequestDTO;
import com.jkomerce.store.dto.PaymentDTO;
import com.jkomerce.store.mapper.OrderMapper;
import com.jkomerce.store.mapper.PaymentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;

    public PaymentService(PaymentMapper paymentMapper, OrderMapper orderMapper) {
        this.paymentMapper = paymentMapper;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public PaymentDTO createPayment(PaymentCreateRequestDTO req){
        // 1) idem key 중복방지
        PaymentDTO existing = paymentMapper.selectPaymentByIdempotencyKey(req.getIdempotencyKey());
        if(existing != null){
            return existing;
        }

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
        if(payment == null) throw new IllegalStateException("결제가 존재하지 않습니다.(PaymentId is null.)");

        if("PAID".equals(payment.getStatus())){
            return payment;
        }

        if(!"REQUESTED".equals(payment.getStatus())){
            throw new IllegalStateException("승인 가능한 결제 상태가 아닙니다.(Status != REQUESTED.)");
        }

        if(payment.getExpiresAt() != null && payment.getExpiresAt().isBefore(LocalDateTime.now())){
            // 결제 시간 만효 시 처리
            paymentMapper.updatePaymentToFailed(paymentId, "EXPIRED");
            orderMapper.updateOrderStatus(payment.getOrderId(), "EXPIRED");
            throw new IllegalStateException("결제 요청이 만료되었습니다.");
        }

        int updated = paymentMapper.updatePaymentToPaid(paymentId, pgTid);
        if(updated == 0) {
            //선점시
            PaymentDTO lastest =paymentMapper.selectPaymentById(paymentId);
            if(lastest != null && "PAID".equals(lastest.getStatus())) return lastest;
            throw new IllegalStateException("결제 승인 처리 실패(상태 변경 불가)");
        }

        orderMapper.updateOrderStatus(payment.getOrderId(), "PAID");

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
