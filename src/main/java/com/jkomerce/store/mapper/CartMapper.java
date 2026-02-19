package com.jkomerce.store.mapper;


import com.jkomerce.store.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartMapper {

    Long selectActiveCartIdByUserId(@Param("userId") Long userId);
    int insertCart(CartDTO cart);
    int increaseCartItemQuantity( @Param("cartId") Long cartId ,
                                  @Param("itemId") Long itemId ,
                                  @Param("quantity") Integer quantity);
    // 삭제된 아이팀을 다시 넣을 경우 upsert
    int upsertCartItem(@Param("cartId") Long cartId,
                       @Param("itemId") Long itemId,
                       @Param("quantity") Integer quantity);
    int updateCartItemQuantity(@Param("cartId") Long cartId,
                               @Param("itemId") Long itemId,
                               @Param("quantity") Integer quantity);
    int deleteCartItem(@Param("cartId") Long cartId,
                       @Param("itemId") Long itemId);
    List<CartItemResponseDTO> selectCartItemsByCartId(@Param("cartId") Long cartId);
    List<CartItemForOrderDTO> selectCartItemsForOrder(@Param("cartId") Long cartId);

    int deleteCartItemsByCartId(@Param("cartId") Long cartId);
}
