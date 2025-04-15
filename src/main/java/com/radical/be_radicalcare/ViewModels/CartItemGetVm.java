package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.CartItem;
import com.radical.be_radicalcare.Dto.VehicleDto;
import lombok.Builder;

import java.text.NumberFormat;
import java.util.Locale;

@Builder
public record CartItemGetVm(
        String id,
        VehicleDto vehicle,
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
    
    // Format price and subtotal for display
    public String getFormattedPrice() {
        return formatCurrency(price);
    }
    
    public String getFormattedSubtotal() {
        return formatCurrency(subtotal);
    }
    
    private static String formatCurrency(Double value) {
        if (value == null) return "0";
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(value);
    }
}
