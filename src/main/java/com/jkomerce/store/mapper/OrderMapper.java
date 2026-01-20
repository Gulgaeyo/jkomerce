package com.jkomerce.store.mapper;


import com.jkomerce.store.dto.OrderDTO;
import com.jkomerce.store.dto.OrderItemDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderMapper {
    int insertOrder(OrderDTO order);
    int insertOrderItem(OrderItemDTO item);

    //Order 조회용
    OrderDTO selectOrderById(Long orderId);
    int updateOrderStatus(@Param("orderId") Long orderId,
                          @Param("status") String status);
}
