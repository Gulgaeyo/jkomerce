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


}
