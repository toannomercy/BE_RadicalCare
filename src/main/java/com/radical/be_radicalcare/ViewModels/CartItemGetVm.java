package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.CartItem;
import com.radical.be_radicalcare.Dto.VehicleDto;
import lombok.Builder;

@Builder
public record CartItemGetVm(
        String id,
        VehicleDto vehicle, // Sử dụng VehicleDto từ DTO package
        String userId,
        Integer quantity,
        Double price,
        Double subtotal
) {
    public static CartItemGetVm from(CartItem cartItem) {
        return CartItemGetVm.builder()
                .id(cartItem.getId())
                .vehicle(cartItem.getVehicleDto()) // Không cần chuyển đổi thêm nếu đã dùng VehicleDto
                .userId(cartItem.getCart().getUserId())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .subtotal(cartItem.getSubtotal())
                .build();
    }
}
