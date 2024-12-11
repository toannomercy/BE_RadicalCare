package com.radical.be_radicalcare.Controllers;

import com.radical.be_radicalcare.Entities.Cart;
import com.radical.be_radicalcare.Entities.CartItem;
import com.radical.be_radicalcare.Services.CartService;
import com.radical.be_radicalcare.ViewModels.CartItemGetVm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CartController {
    private final CartService cartService;

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping("/cart/add")
    public ResponseEntity<?> addItemToCart(@RequestBody CartItemGetVm cartItemVm) {
        try {
            cartService.addItemToCart(cartItemVm);
            Map<String, Object> response = new HashMap<>();
            response.put("status", 201);
            response.put("message", "Item added to cart successfully");
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "Error adding item to cart: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/cart/{userId}")
    public ResponseEntity<?> getCartItems(@PathVariable String userId) {
        try {
            Cart cart = cartService.getCartByUserId(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Cart retrieved successfully");
            response.put("data", cart);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "Error retrieving cart: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PutMapping("/cart/update")
    public ResponseEntity<?> updateCartItem(@RequestBody CartItemGetVm cartItemVm) {
        try {
            cartService.updateCartItem(cartItemVm);
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Cart item updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "Error updating cart item: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @DeleteMapping("/cart/remove/{itemId}")
    public ResponseEntity<?> removeItemFromCart(@PathVariable String itemId) {
        try {
            cartService.removeItemFromCart(itemId);
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Item removed from cart successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "Error removing item from cart: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @DeleteMapping("/cart/clear/{userId}")
    public ResponseEntity<?> clearCart(@PathVariable String userId) {
        try {
            cartService.clearCart(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Cart cleared successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "Error clearing cart: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
