package com.jkomerce.store.batch;

import com.jkomerce.store.service.PaymentExpireBatchService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentExpireScheduler {

    private final PaymentExpireBatchService paymentExpireBatchService;

    @Value("${batch.payment-expire.limit:200}")
    private int limit;

    @Scheduled(
            fixedDelayString = "${batch.payment-expire.fixed-delay-ms:30000}",
            initialDelayString = "${batch.payment-expire.initial-delay-ms:10000}"
    )
    public void run(){
        int expired = paymentExpireBatchService.expireOnce(limit);
        if (expired > 0){
            System.out.println("Expired " + expired + " payments.");
        }
    }

}
