// Updated CartController.java
package com.radical.be_radicalcare.Controllers;

import com.radical.be_radicalcare.Entities.Cart;
import com.radical.be_radicalcare.Services.CartService;
import com.radical.be_radicalcare.ViewModels.CartGetVm;
import com.radical.be_radicalcare.ViewModels.CartItemGetVm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Slf4j
@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // Get Transient Cart
    @GetMapping("/temporary/{userId}")
    public ResponseEntity<Map<String, Object>> getCartItems(@PathVariable String userId) {
        // Lấy Cart từ Redis
        Cart cart = cartService.getTemporaryCart(userId);

        if (cart == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message", "Cart not found for userId: " + userId,
                            "status", 404
                    ));
        }

        // Chuyển đổi CartItems sang CartItemGetVm
        List<CartItemGetVm> cartItems = cart.getItems().stream()
                .map(CartItemGetVm::from)
                .toList();

        // Chuẩn bị phản hồi JSON
        Map<String, Object> response = Map.of(
                "data", Map.of(
                        "id", cart.getId(),
                        "userId", cart.getUserId(),
                        "totalCost", cart.getTotalCost(),
                        "items", cartItems
                ),
                "message", "Cart retrieved successfully",
                "status", 200
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/temporary/add")
    public ResponseEntity<String> addCartItem(
            @RequestParam String userId,
            @RequestParam String chassisNumber,
            @RequestParam int quantity) {
        cartService.saveTemporaryCart(userId, chassisNumber, quantity);
        return ResponseEntity.ok("Item added to cart successfully");
    }

    @PatchMapping("/temporary/update")
    public ResponseEntity<String> updateCartItemQuantity(@RequestBody Map<String, Object> payload) {
        String userId = (String) payload.get("userId");
        String cartItemId = (String) payload.get("cartItemId");
        int newQuantity = (int) payload.get("newQuantity");

        cartService.updateCartItemQuantity(userId, cartItemId, newQuantity);
        return ResponseEntity.ok("CartItem quantity updated successfully");
    }

    @PostMapping("/temporary/cleanup/{userId}")
    public ResponseEntity<Map<String, Object>> cleanUpTemporaryCart(@PathVariable String userId) {
        cartService.cleanUpCart(userId);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Temporary cart cleaned up successfully for userId: " + userId,
                        "status", 200
                )
        );
    }

    @DeleteMapping("/temporary/{userId}/item/{cartItemId}")
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

    // Save Persistent Cart (from Transient Cart)
//    @PostMapping("/persistent/{userId}")
//    public ResponseEntity<String> savePersistentCart(@PathVariable String userId) {
//        cartService.savePersistentCart(userId);
//        return ResponseEntity.ok("Cart saved to database successfully");
//    }

    // Get Persistent Cart
//    @GetMapping("/persistent/{userId}")
//    public ResponseEntity<CartGetVm> getPersistentCart(@PathVariable String userId) {
//        Cart cart = cartService.getPersistentCart(userId);
//
//        // Chuyển đổi CartItems sang CartItemGetVm
//        List<CartItemGetVm> cartItems = cart.getItems().stream()
//                .map(CartItemGetVm::from)
//                .collect(Collectors.toList());
//
//        // Tạo CartGetVm từ Cart và danh sách CartItemGetVm
//        return ResponseEntity.ok(CartGetVm.fromEntity(cart, cartItems));
//    }


    // Remove Vehicle from Transient Cart
//    @DeleteMapping("/temporary/{userId}/vehicle/{chassisNumber}")
//    public ResponseEntity<String> removeVehicleFromTemporaryCart(@PathVariable String userId, @PathVariable String chassisNumber) {
//        cartService.removeVehicleFromTemporaryCart(userId, chassisNumber);
//        return ResponseEntity.ok("Vehicle removed from temporary cart successfully");
//    }
//
//    // Remove Vehicle from Persistent Cart
//    @DeleteMapping("/persistent/{userId}/vehicle/{chassisNumber}")
//    public ResponseEntity<String> removeVehicleFromPersistentCart(@PathVariable String userId, @PathVariable String chassisNumber) {
//        cartService.removeVehicleFromPersistentCart(userId, chassisNumber);
//        return ResponseEntity.ok("Vehicle removed from persistent cart successfully");
//    }
}
