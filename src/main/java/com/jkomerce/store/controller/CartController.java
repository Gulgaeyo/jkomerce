package com.jkomerce.store.controller;

import com.jkomerce.store.dto.CartItemAddRequestDTO;
import com.jkomerce.store.dto.CartItemDTO;
import com.jkomerce.store.dto.CartItemResponseDTO;
import com.jkomerce.store.dto.CartItemUpdateRequestDTO;
import com.jkomerce.store.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    //담기 누적
    @PostMapping("/items")
    public List<CartItemResponseDTO> addItem(@RequestBody CartItemAddRequestDTO req, HttpSession session) {
        return cartService.addItem(req, session);
    }

    @GetMapping("/items")
    public List<CartItemResponseDTO> myCartItems(HttpSession session){
        return cartService.getMyCartItems(session);
    }

    @PatchMapping("/items/{itemId}")
    public List<CartItemResponseDTO> updateQuantity(@PathVariable Long itemId,
                                            @RequestBody CartItemUpdateRequestDTO req,
                                            HttpSession session) {
        Integer q = (req == null) ? null : req.getQuantity();
        return cartService.updateItemQuantity(itemId, q, session);
    }

    @DeleteMapping("/items")
    public List<CartItemDTO> clearMyCart(HttpSession session){
        return cartService.clearMyCart(session);
    }

    @DeleteMapping("/items/{itemId}")
    public List<CartItemResponseDTO> deleteCartItem(@PathVariable Long itemId, HttpSession session) {
        return cartService.deleteCartItem(session, itemId);
    }

}
