package com.jkomerce.store.mapper;


import com.jkomerce.store.dto.OrderDTO;
import com.jkomerce.store.dto.OrderItemDTO;
import com.jkomerce.store.dto.OrderItemStockDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    int insertOrder(OrderDTO order);
    int insertOrderItem(OrderItemDTO item);

    //Order 조회용
    OrderDTO selectOrderById(Long orderId);
    int updateOrderStatus(@Param("orderId") Long orderId,
                          @Param("status") String status);

    List<OrderItemStockDTO> selectOrderItemsForStock(@Param("orderId") long orderId);

    // 주문 조회
    OrderDTO selectOrderByIdAndUserId(@Param("orderId") Long orderId,
                                      @Param("userId") Long userId);

    List<OrderItemDTO> selectOrderItemsByOrderId(@Param("orderId") Long orderId);
    List<OrderDTO> selectOrdersByUserId(@Param("userId") Long userId,
                                       @Param("limit") int limit, @Param("offset") int offset, @Param("status") String status);

    int updateOrderTotalAmount(@Param("orderId") Long orderId, @Param("totalAmount") Long totalAmount);
    // 주문 멱등성 (중복 방어)
    OrderDTO selectOrderByIdempotencyKey(@Param("idempotencyKey") String idempotencyKey, @Param("userId") Long userId);
}
