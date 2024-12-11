package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.CartItem;
import lombok.Builder;

@Builder
public record CartItemGetVm(
        String id,
        VehicleGetVm vehicle,
        String userId, // Thêm userId vào đây
        Integer quantity,
        Double price,
        Double subtotal
) {
    public static CartItemGetVm from(CartItem cartItem) {
        return CartItemGetVm.builder()
                .id(cartItem.getId())
                .vehicle(VehicleGetVm.fromEntity(cartItem.getVehicle()))
                .userId(cartItem.getCart().getUserId()) // Gán userId từ Cart
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .subtotal(cartItem.getSubtotal())
                .build();
    }
}
