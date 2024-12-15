package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.CartItem;
import lombok.Builder;

@Builder
public record CartItemGetVm(
        String id,
        VehicleDtoGetVm vehicle, // Sử dụng VehicleDtoGetVm thay vì VehicleGetVm
        String userId,
        Integer quantity,
        Double price,
        Double subtotal
) {
    public static CartItemGetVm from(CartItem cartItem) {
        return CartItemGetVm.builder()
                .id(cartItem.getId())
                .vehicle(VehicleDtoGetVm.fromDto(cartItem.getVehicleDto())) // Sử dụng fromDto
                .userId(cartItem.getCart().getUserId())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .subtotal(cartItem.getSubtotal())
                .build();
    }
}

