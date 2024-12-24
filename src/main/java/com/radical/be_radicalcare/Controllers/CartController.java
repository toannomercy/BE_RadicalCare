package com.radical.be_radicalcare.Controllers;

import com.radical.be_radicalcare.Entities.Cart;
import com.radical.be_radicalcare.Services.CartService;
import com.radical.be_radicalcare.ViewModels.CartItemGetVm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CartController {
    private final CartService cartService;

    @PostMapping("/cart/add")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<?> addItemToCart(@RequestBody CartItemGetVm cartItemVm, Authentication authentication) {
        log.info("Received request to add item to cart: {}", cartItemVm);

        try {
            // Handle userId from authentication if not provided
            if (cartItemVm.userId() == null || cartItemVm.userId().isEmpty()) {
                cartItemVm = CartItemGetVm.builder()
                        .id(cartItemVm.id())
                        .vehicle(cartItemVm.vehicle())
                        .userId(authentication.getName())
                        .quantity(cartItemVm.quantity())
                        .price(cartItemVm.price())
                        .subtotal(cartItemVm.subtotal())
                        .build();
                log.debug("Updated cartItemVm with userId from authentication: {}", cartItemVm);
            }

            cartService.addItemToCart(cartItemVm);
            log.info("Item added to cart successfully for user: {}", cartItemVm.userId());

            Map<String, Object> response = new HashMap<>();
            response.put("status", 201);
            response.put("message", "Item added to cart successfully");
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            log.error("Failed to add item to cart: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "Error adding item to cart: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/cart/{userId}")
    public ResponseEntity<?> getCartItems(@PathVariable String userId) {
        log.info("Received request to get cart items for userId: {}", userId);

        try {
            Cart cart = cartService.getCartByUserId(userId);
            log.info("Cart retrieved successfully for userId: {}", userId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Cart retrieved successfully");
            response.put("data", cart);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to retrieve cart for userId {}: {}", userId, e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "Error retrieving cart: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PutMapping("/cart/update")
    public ResponseEntity<?> updateCartItem(@RequestBody CartItemGetVm cartItemVm) {
        log.info("Received request to update cart item: {}", cartItemVm);

        try {
            cartService.updateCartItem(cartItemVm);
            log.info("Cart item updated successfully: {}", cartItemVm.id());

            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Cart item updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to update cart item {}: {}", cartItemVm.id(), e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "Error updating cart item: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/cart/remove/{itemId}")
    public ResponseEntity<?> removeItemFromCart(@PathVariable String itemId) {
        log.info("Received request to remove item from cart: {}", itemId);

        try {
            cartService.removeItemFromCart(itemId);
            log.info("Item removed from cart successfully: {}", itemId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Item removed from cart successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to remove item {} from cart: {}", itemId, e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "Error removing item from cart: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/cart/clear/{userId}")
    public ResponseEntity<?> clearCart(@PathVariable String userId) {
        log.info("Received request to clear cart for userId: {}", userId);

        try {
            cartService.clearCart(userId);
            log.info("Cart cleared successfully for userId: {}", userId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Cart cleared successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to clear cart for userId {}: {}", userId, e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "Error clearing cart: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}