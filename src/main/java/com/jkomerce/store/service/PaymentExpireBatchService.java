package com.jkomerce.store.service;

import com.jkomerce.store.dto.PaymentDTO;
import com.jkomerce.store.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentExpireBatchService {

    private final PaymentMapper paymentMapper;
    private final PaymentFailService paymentFailService;

    public int expireOnce(int limit){
        List<PaymentDTO> targets = paymentMapper.selectExpiredRequestedPayments(limit);
        int expiredCount = 0;

        for (PaymentDTO p : targets) {
            boolean changed = paymentFailService.expirePaymentAndOrder(p.getPaymentId(), p.getOrderId());
            if(changed) {
                expiredCount++;
            }
        }
        return expiredCount;

    }


}
