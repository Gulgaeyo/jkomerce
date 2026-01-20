package com.jkomerce.store.controller;

import com.jkomerce.store.dto.PaymentApproveRequestDTO;
import com.jkomerce.store.dto.PaymentCreateRequestDTO;
import com.jkomerce.store.dto.PaymentDTO;
import com.jkomerce.store.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PaymentCreateRequestDTO req){
        PaymentDTO payment = paymentService.createPayment(req);
        return ResponseEntity.status(201).body(payment);
    }

    @PostMapping("/{paymentId}/approve")
    public ResponseEntity<?> approve(@PathVariable Long paymentId,
                                     @RequestBody(required = false) PaymentApproveRequestDTO req){
        String pgTid = (req == null) ? null : req.getPgTid();
        PaymentDTO payment = paymentService.approvePayment(paymentId, pgTid);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/{paymentId}/fail")
    public ResponseEntity<?> fail(@PathVariable Long paymentId,
                                  @RequestBody PaymentApproveRequestDTO req){
        String reason = (req == null || req.getPgTid() == null) ? "FAILED" : req.getPgTid();
        PaymentDTO payment = paymentService.failPayment(paymentId, reason);
        return ResponseEntity.ok(payment);
    }

}
