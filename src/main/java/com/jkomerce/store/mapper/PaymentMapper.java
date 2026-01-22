package com.jkomerce.store.mapper;

import com.jkomerce.store.dto.PaymentDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaymentMapper {

    int insertPayment(PaymentDTO payment);

    PaymentDTO selectPaymentById(@Param("paymentId") Long paymentId);

    PaymentDTO selectPaymentByIdempotencyKey(@Param("idempotencyKey") String idempotencyKey);

    int updatePaymentToPaid(@Param("paymentId") Long paymentId,
                            @Param("pgTid") String pgTid);

    int updatePaymentToFailed(@Param("paymentId") Long paymentId,
                              @Param("failReason") String failReason);

    PaymentDTO selectActiveRequestedByOrderId(@Param("orderId") Long orderId);
}
