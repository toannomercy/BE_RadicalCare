package com.radical.be_radicalcare.Services;

import com.radical.be_radicalcare.Entities.Cart;
import com.radical.be_radicalcare.Entities.CartItem;
import com.radical.be_radicalcare.Entities.Vehicle;
import com.radical.be_radicalcare.Repositories.ICartItemRepository;
import com.radical.be_radicalcare.Repositories.ICartRepository;
import com.radical.be_radicalcare.Repositories.IVehicleRepository;
import com.radical.be_radicalcare.ViewModels.CartItemGetVm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final ICartRepository cartRepository;
    private final ICartItemRepository cartItemRepository;
    private final IVehicleRepository vehicleRepository;

    @Transactional
    public void addItemToCart(CartItemGetVm cartItemVm) {
        Cart cart = cartRepository.findByUserId(cartItemVm.userId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(cartItemVm.userId()); // Lấy đúng userId từ JSON
                    return cartRepository.save(newCart);
                });

        // Find the vehicle
        Vehicle vehicle = vehicleRepository.findById(cartItemVm.vehicle().chassisNumber())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        // Lấy giá từ xe
        double price = vehicle.getPrice(); // Giá được lấy từ thông tin xe

        // Check if the item already exists in the cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndVehicle(cart.getId(), vehicle.getChassisNumber());
        if (existingItem.isPresent()) {
            // Update quantity and subtotal if the item already exists
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + cartItemVm.quantity());
            item.setSubtotal(item.getQuantity() * price); // Tự động tính subtotal
            cartItemRepository.save(item);
        } else {
            // Add new item to the cart
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .vehicle(vehicle)
                    .quantity(cartItemVm.quantity())
                    .price(price) // Gắn giá từ xe
                    .subtotal(cartItemVm.quantity() * price) // Tự động tính subtotal
                    .build();
            cartItemRepository.save(newItem);
        }

        // Update cart total cost
        updateCartTotalCost(cart);
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