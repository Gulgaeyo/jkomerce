package com.jkomerce.store.service;

import com.jkomerce.store.domain.OrderStatus;
import com.jkomerce.store.domain.PaymentStatus;
import com.jkomerce.store.mapper.OrderMapper;
import com.jkomerce.store.mapper.PaymentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentFailService {

    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;

    public PaymentFailService(PaymentMapper paymentMapper, OrderMapper orderMapper) {
        this.paymentMapper = paymentMapper;
        this.orderMapper = orderMapper;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failPaymentAndCancelOrder(Long paymentId, Long orderId, String reason){
        paymentMapper.updatePaymentToFailed(paymentId, reason);
        orderMapper.updateOrderStatus(orderId, OrderStatus.CANCELED.toDbValue());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean expirePaymentAndOrder(Long paymentId, Long orderId){
        int p = paymentMapper.updatePaymentToExpired(
          paymentId,
          PaymentStatus.EXPIRED.toDbValue()
        );
        if (p == 0) return false;
        orderMapper.updateOrderToExpiredIfPending(orderId, OrderStatus.EXPIRED.toDbValue());
        return true;
    }
}
