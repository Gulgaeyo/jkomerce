package com.jkomerce.store.service;

import com.jkomerce.store.dto.CartDTO;
import com.jkomerce.store.dto.CartItemAddRequestDTO;
import com.jkomerce.store.dto.CartItemDTO;
import com.jkomerce.store.dto.UserDTO;
import com.jkomerce.store.exception.UnauthorizedException;
import com.jkomerce.store.mapper.CartMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartMapper cartMapper;

    @Transactional
    public Long getOrCreateCartId(Integer userId){
        Long cartId = cartMapper.selectActiveCartIdByUserId(userId);

        if(cartId == null){
            CartDTO cart = new CartDTO();
            cart.setUserId(userId);
            cartMapper.insertCart(cart);
            Long newCartId = cart.getCartId();
            if (newCartId == null) throw new IllegalStateException("cartId 생성 실패");
            return newCartId;
        }

        return cartId;
    }

    @Transactional
    public List<CartItemDTO> addItem(CartItemAddRequestDTO req, HttpSession session) {
        Integer userId = getUserIdFromSession(session);

        // 요청 검증
        if(req == null) throw new IllegalArgumentException("요청이 비어있습니다.");
        if(req.getItemId() == null) throw new IllegalArgumentException("itemId가 필요합니다.");
        if(req.getQuantity() == null || req.getQuantity() <= 0)
            throw new IllegalArgumentException("수량이 올바르지 않습니다.");

        Long cartId = getOrCreateCartId(userId);

        int affected = cartMapper.upsertCartItem(cartId, req.getItemId(), req.getQuantity());
        if(affected == 0) throw new IllegalStateException("장바구니 반영 실패");

        return cartMapper.selectCartItemsByCartId(cartId);
    }





    /* 세션에서 userId 가져오기*/
    private Integer getUserIdFromSession(HttpSession session) {
        Object v = session.getAttribute("userId");
        if(v instanceof Integer) return (Integer) v;

        // 프로젝트마다 세션에 UserDTO를 넣는 경우도 있어서 fallback
        Object userObj = session.getAttribute("user");
        if (userObj instanceof UserDTO) return ((UserDTO) userObj).getId();

        throw new UnauthorizedException("로그인이 필요합니다.");
    }


    public List<CartItemDTO> getMyCartItems(HttpSession session){
        Integer userId = getUserIdFromSession(session);

        Long cartId = cartMapper.selectActiveCartIdByUserId(userId);

        // cart가 존재하지 않을 때 반응 200 + 빈 리스트
        if(cartId == null) return Collections.emptyList();

        return cartMapper.selectCartItemsByCartId(cartId);
    }

    @Transactional
    public List<CartItemDTO> updateItemQuantity(Long itemId, Integer quantity, HttpSession session){
        Integer userId = getUserIdFromSession(session);

        if(itemId == null) throw new IllegalArgumentException("itemId가 필요압니다.");
        if(quantity == null) throw new IllegalArgumentException("quantity가 핑료합니다.");
        if(quantity < 0) throw new IllegalArgumentException("quantity는 0 이상이어야 합니다.");

        Long cartId = cartMapper.selectActiveCartIdByUserId(userId);
        if(cartId == null) return Collections.emptyList();

        if(quantity == 0) {
            cartMapper.deleteCartItem(cartId, itemId);
        } else {
            cartMapper.updateCartItemQuantity(cartId, itemId, quantity);
        }

        return cartMapper.selectCartItemsByCartId(cartId);


    }

    
}
