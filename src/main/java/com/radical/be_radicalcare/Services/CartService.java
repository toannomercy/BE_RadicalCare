package com.radical.be_radicalcare.Services;

import com.radical.be_radicalcare.Entities.Cart;
import com.radical.be_radicalcare.Entities.CartItem;
import com.radical.be_radicalcare.Entities.Vehicle;
import com.radical.be_radicalcare.Repositories.ICartItemRepository;
import com.radical.be_radicalcare.Repositories.ICartRepository;
import com.radical.be_radicalcare.Repositories.IVehicleRepository;
import com.radical.be_radicalcare.ViewModels.CartItemGetVm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final ICartRepository cartRepository;
    private final ICartItemRepository cartItemRepository;
    private final IVehicleRepository vehicleRepository;

    @Transactional
    public void addItemToCart(CartItemGetVm cartItemVm) {
        // Tìm giỏ hàng theo userId
        Cart cart = cartRepository.findByUserId(cartItemVm.userId())
                .orElseGet(() -> {
                    // Tạo giỏ hàng mới nếu không tồn tại
                    Cart newCart = new Cart();
                    newCart.setUserId(cartItemVm.userId());
                    newCart.setTotalCost(0.0);
                    return cartRepository.save(newCart);
                });

        // Thêm sản phẩm vào giỏ hàng
        Vehicle vehicle = vehicleRepository.findByChassisNumber(cartItemVm.vehicle().chassisNumber())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setVehicle(vehicle);
        cartItem.setQuantity(cartItemVm.quantity());
        cartItem.setPrice(cartItemVm.price());
        cartItem.setSubtotal(cartItemVm.quantity() * cartItemVm.price());
        cartItemRepository.save(cartItem);

        // Cập nhật tổng giá trị của giỏ hàng
        cart.setTotalCost(cart.getTotalCost() + cartItem.getSubtotal());
        cartRepository.save(cart);
    }

    public Cart getCartByUserId(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
    }

    @Transactional
    public void updateCartItem(CartItemGetVm cartItemVm) {
        Cart cart = cartRepository.findByUserId(cartItemVm.vehicle().chassisNumber())
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        Vehicle vehicle = vehicleRepository.findById(cartItemVm.vehicle().chassisNumber())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        CartItem item = cartItemRepository.findByCartAndVehicle(cart.getId(), vehicle.getChassisNumber())
                .orElseThrow(() -> new IllegalArgumentException("Item not found in cart"));

        item.setQuantity(cartItemVm.quantity());
        item.setSubtotal(cartItemVm.quantity() * item.getPrice());
        cartItemRepository.save(item);

        // Update cart total cost
        updateCartTotalCost(cart);
    }

    @Transactional
    public void removeItemFromCart(String itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        Cart cart = item.getCart();
        cartItemRepository.delete(item);

        // Update cart total cost
        updateCartTotalCost(cart);
    }

    @Transactional
    public void clearCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        cartItemRepository.deleteAllByCart(cart);

        // Reset cart total cost
        cart.setTotalCost(0.0);
        cartRepository.save(cart);
    }

    private void updateCartTotalCost(Cart cart) {
        double totalCost = cartItemRepository.findAllByCart(cart).stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
        cart.setTotalCost(totalCost);
        cartRepository.save(cart);
    }
}