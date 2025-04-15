package com.radical.be_radicalcare.Controllers;

import com.radical.be_radicalcare.Entities.Cart;
import com.radical.be_radicalcare.Services.CartService;
import com.radical.be_radicalcare.ViewModels.CartItemGetVm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/cart/add-web")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<?> addItemToCart(@RequestBody CartItemGetVm cartItemVm, Authentication authentication) {
        log.info("[POST] /cart/add-web - Payload received: {}", cartItemVm);

        try {
            // Nếu userId không được cung cấp, lấy từ Authentication
            if (cartItemVm.userId() == null || cartItemVm.userId().isEmpty()) {
                cartItemVm = CartItemGetVm.builder()
                        .id(cartItemVm.id())
                        .vehicle(cartItemVm.vehicle())
                        .userId(authentication.getName())
                        .quantity(cartItemVm.quantity())
                        .price(cartItemVm.price())
                        .subtotal(cartItemVm.subtotal())
                        .build();
                log.debug("Updated CartItemGetVm: {}", cartItemVm);
            }

            // Xử lý logic thêm sản phẩm vào giỏ hàng
            cartService.addItemToCart(cartItemVm);
            log.info("Item added to cart successfully for user: {}", cartItemVm.userId());

            Map<String, Object> response = new HashMap<>();
            response.put("status", 201);
            response.put("message", "Item added to cart successfully");
            return ResponseEntity.status(201).body(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid input: {}", e.getMessage(), e);
            return ResponseEntity.status(400).body(Map.of(
                    "status", 400,
                    "message", "Invalid input: " + e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error adding item to cart: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                    "status", 500,
                    "message", "Error adding item to cart: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/cart/{userId}")
    public ResponseEntity<?> getCartItemsFromDb(@PathVariable String userId) {
        log.info("Received request to get cart items from database for userId: {}", userId);

        try {
            Cart cart = cartService.getCartByUserId(userId);
            log.info("Cart retrieved successfully for userId: {}", userId);

            List<CartItemGetVm> cartItems = cart.getItems().stream()
                    .map(CartItemGetVm::from)
                    .toList();

            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Cart retrieved successfully");
            response.put("data", Map.of(
                    "id", cart.getId(),
                    "userId", cart.getUserId(),
                    "totalCost", cart.getTotalCost(),
                    "formattedTotalCost", formatCurrency(cart.getTotalCost()),
                    "items", cartItems.stream().map(item -> Map.of(
                            "id", item.id(),
                            "vehicle", item.vehicle(),
                            "userId", item.userId(),
                            "quantity", item.quantity(),
                            "price", item.price(),
                            "formattedPrice", item.getFormattedPrice(),
                            "subtotal", item.subtotal(),
                            "formattedSubtotal", item.getFormattedSubtotal()
                    )).toList()
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to retrieve cart for userId {}: {}", userId, e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "Error retrieving cart: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/cart/temporary/{userId}")
    public ResponseEntity<Map<String, Object>> getTemporaryCartItems(@PathVariable String userId) {
        log.info("Received request to get temporary cart items from Redis for userId: {}", userId);

        Cart cart = cartService.getTemporaryCart(userId);

        if (cart == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message", "Cart not found for userId: " + userId,
                            "status", 404
                    ));
        }

        List<CartItemGetVm> cartItems = cart.getItems().stream()
                .map(CartItemGetVm::from)
                .toList();

        Map<String, Object> response = Map.of(
                "data", Map.of(
                        "id", cart.getId(),
                        "userId", cart.getUserId(),
                        "totalCost", cart.getTotalCost(),
                        "formattedTotalCost", formatCurrency(cart.getTotalCost()),
                        "items", cartItems.stream().map(item -> Map.of(
                                "id", item.id(),
                                "vehicle", item.vehicle(),
                                "userId", item.userId(),
                                "quantity", item.quantity(),
                                "price", item.price(),
                                "formattedPrice", item.getFormattedPrice(),
                                "subtotal", item.subtotal(),
                                "formattedSubtotal", item.getFormattedSubtotal()
                        )).toList()
                ),
                "message", "Cart retrieved successfully",
                "status", 200
        );

        return ResponseEntity.ok(response);
    }

    // Helper method for formatting currency
    private String formatCurrency(Double value) {
        if (value == null) return "0";
        java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
        return formatter.format(value);
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

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
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

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
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

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping("/cart/temporary/add")
    public ResponseEntity<String> addCartItem(
            @RequestParam String userId,
            @RequestParam String chassisNumber,
            @RequestParam int quantity) {
        log.info("Adding item to cart - userId: {}, chassisNumber: {}, quantity: {}", userId, chassisNumber, quantity);
        cartService.saveTemporaryCart(userId, chassisNumber, quantity);
        return ResponseEntity.ok("Item added to cart successfully");
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PatchMapping("/cart/temporary/update")
    public ResponseEntity<String> updateCartItemQuantity(@RequestBody Map<String, Object> payload) {
        String userId = (String) payload.get("userId");
        String cartItemId = (String) payload.get("cartItemId");
        int newQuantity = (int) payload.get("newQuantity");

        cartService.updateCartItemQuantity(userId, cartItemId, newQuantity);
        return ResponseEntity.ok("CartItem quantity updated successfully");
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping("/cart/temporary/cleanup/{userId}")
    public ResponseEntity<Map<String, Object>> cleanUpTemporaryCart(@PathVariable String userId) {
        cartService.cleanUpCart(userId);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Temporary cart cleaned up successfully for userId: " + userId,
                        "status", 200
                )
        );
    }

    @DeleteMapping("/cart/temporary/{userId}/item/{cartItemId}")
    public ResponseEntity<Map<String, Object>> removeCartItem(
            @PathVariable String userId,
            @PathVariable String cartItemId) {
        cartService.removeCartItem(userId, cartItemId);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Item removed successfully from cart",
                        "status", 200
                )
        );
    }

}
