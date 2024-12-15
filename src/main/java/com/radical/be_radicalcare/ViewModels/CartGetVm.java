package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.Cart;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record CartGetVm(
        String id,
        String userId,
        Double totalCost,
        List<CartItemGetVm> items
) {
    public static CartGetVm fromEntity(Cart cart, List<CartItemGetVm> cartItems) {
        return CartGetVm.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .totalCost(cart.getTotalCost())
                .items(cartItems)
                .build();
    }
}

