package com.radical.be_radicalcare.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.radical.be_radicalcare.Dto.VehicleDto;
import com.radical.be_radicalcare.Entities.Cart;
import com.radical.be_radicalcare.Entities.CartItem;
import com.radical.be_radicalcare.Entities.Vehicle;
import com.radical.be_radicalcare.Entities.VehicleImage;
import com.radical.be_radicalcare.Repositories.ICartItemRepository;
import com.radical.be_radicalcare.Repositories.ICartRepository;
import com.radical.be_radicalcare.Repositories.IVehicleRepository;
import com.radical.be_radicalcare.ViewModels.CartItemGetVm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartService {

    @Autowired
    private ICartRepository cartRepository;
    @Autowired
    private IVehicleRepository vehicleRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ICartItemRepository cartItemRepository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CART_CACHE_PREFIX = "cart:";

    private VehicleDto toVehicleDto(Vehicle vehicle) {
        List<String> imageUrls = vehicle.getVehicleImages() != null
                ? vehicle.getVehicleImages().stream().map(VehicleImage::getImageUrl).collect(Collectors.toList())
                : new ArrayList<>();

        return VehicleDto.builder()
                .chassisNumber(vehicle.getChassisNumber())
                .vehicleName(vehicle.getVehicleName())
                .importDate(vehicle.getImportDate())
                .version(vehicle.getVersion())
                .color(vehicle.getColor())
                .segment(vehicle.getSegment())
                .isDeleted(vehicle.getIsDeleted())
                .sold(vehicle.getSold())
                .price(vehicle.getPrice())
                .description(vehicle.getDescription())
                .imageUrls(imageUrls)
                .build();
    }

    public Cart getTemporaryCart(String userId) {
        String cacheKey = CART_CACHE_PREFIX + userId;

        // Lấy dữ liệu từ Redis
        Object cachedCart = redisTemplate.opsForValue().get(cacheKey);

        Cart cart = null;

        if (cachedCart instanceof LinkedHashMap) {
            // Chuyển đổi từ LinkedHashMap sang Cart
            cart = objectMapper.convertValue(cachedCart, Cart.class);

            if (cart != null && cart.getItems() != null) {
                // Xử lý các CartItem
                cart.getItems().forEach(item -> {
                    if (item.getVehicleDto() == null) {
                        log.warn("VehicleDto is null for CartItem: {}", item.getId());
                    } else {
                        log.info("Deserialized VehicleDto: {}", item.getVehicleDto());
                    }
                });
            }
        } else if (cachedCart instanceof Cart) {
            cart = (Cart) cachedCart;

            if (cart.getItems() != null) {
                cart.getItems().forEach(item -> {
                    if (item.getVehicleDto() == null) {
                        log.warn("VehicleDto is null for CartItem: {}", item.getId());
                    } else {
                        log.info("Deserialized VehicleDto: {}", item.getVehicleDto());
                    }
                });
            }
        } else {
            log.warn("No cart found for user ID: {}", userId);
        }

        return cart;
    }


    public void saveTemporaryCart(String userId, String chassisNumber, int quantity) {
        String cacheKey = CART_CACHE_PREFIX + userId;

        // Lấy giỏ hàng tạm thời từ Redis
        Object cachedCart = redisTemplate.opsForValue().get(cacheKey);
        Cart cart;
        if (cachedCart instanceof LinkedHashMap) {
            cart = objectMapper.convertValue(cachedCart, Cart.class);
        } else if (cachedCart instanceof Cart) {
            cart = (Cart) cachedCart;
        } else {
            cart = new Cart();
            cart.setId(UUID.randomUUID().toString());
            cart.setUserId(userId);
            cart.setItems(new ArrayList<>());
            log.info("Created new cart with ID: {}", cart.getId());
        }

        // Lấy thông tin Vehicle từ database
        Vehicle vehicle = vehicleRepository.findById(chassisNumber)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with chassis number: " + chassisNumber));

        // Chuyển đổi Vehicle sang VehicleDto
        VehicleDto vehicleDto = toVehicleDto(vehicle);

        // Kiểm tra xem sản phẩm đã tồn tại trong giỏ hàng hay chưa
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getVehicleDto().getChassisNumber().equals(chassisNumber))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Cập nhật số lượng nếu sản phẩm đã tồn tại
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setSubtotal(existingItem.getPrice() * existingItem.getQuantity());
        } else {
            // Thêm sản phẩm mới nếu chưa tồn tại
            CartItem newItem = new CartItem();
            newItem.setId(UUID.randomUUID().toString());
            newItem.setVehicleDto(vehicleDto);
            newItem.setQuantity(quantity);
            newItem.setPrice(vehicleDto.getPrice());
            newItem.setSubtotal(vehicleDto.getPrice() * quantity);
            cart.getItems().add(newItem);
        }

        // Cập nhật tổng chi phí giỏ hàng
        double totalCost = cart.getItems().stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
        cart.setTotalCost(totalCost);

        // Lưu giỏ hàng vào Redis
        redisTemplate.opsForValue().set(cacheKey, cart, 30, TimeUnit.MINUTES);
        log.info("Cart saved to Redis for userId: {}", userId);
    }

    public void updateCartItemQuantity(String userId, String cartItemId, int newQuantity) {
        String cacheKey = CART_CACHE_PREFIX + userId;

        // Lấy Cart từ Redis
        Cart cart = getTemporaryCart(userId);

        if (cart == null) {
            throw new IllegalArgumentException("Cart not found for userId: " + userId);
        }

        // Tìm CartItem cần cập nhật
        CartItem itemToUpdate = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("CartItem not found: " + cartItemId));

        // Cập nhật quantity và subtotal
        itemToUpdate.setQuantity(newQuantity);
        itemToUpdate.setSubtotal(itemToUpdate.getPrice() * newQuantity);

        // Tính lại tổng chi phí của giỏ hàng
        double totalCost = cart.getItems().stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
        cart.setTotalCost(totalCost);

        // Lưu lại Cart vào Redis
        redisTemplate.opsForValue().set(cacheKey, cart, 30, TimeUnit.MINUTES);
        log.info("Updated CartItem quantity in Redis: {}", itemToUpdate);
    }

    public void cleanUpCart(String userId) {
        String cacheKey = CART_CACHE_PREFIX + userId;
        Cart cart = getTemporaryCart(userId);

        if (cart != null && cart.getItems() != null) {
            cart.getItems().forEach(item -> {
                if (item.getVehicleDto() == null) {
                    // Tìm Vehicle từ database và tạo VehicleDto
                    Vehicle vehicle = vehicleRepository.findById(item.getId())
                            .orElse(null);

                    if (vehicle != null) {
                        item.setVehicleDto(toVehicleDto(vehicle));
                        log.info("Updated VehicleDto for CartItem: {}", item.getId());
                    } else {
                        log.warn("Vehicle not found for CartItem: {}", item.getId());
                    }
                }
            });

            // Lưu lại cart đã được cập nhật
            redisTemplate.opsForValue().set(cacheKey, cart, 30, TimeUnit.MINUTES);
        }

    }

    public void removeCartItem(String userId, String cartItemId) {
        String cacheKey = CART_CACHE_PREFIX + userId;

        // Lấy giỏ hàng từ Redis
        Cart cart = getTemporaryCart(userId);
        if (cart == null) {
            throw new IllegalArgumentException("Cart not found for userId: " + userId);
        }

        // Xóa sản phẩm khỏi danh sách
        cart.getItems().removeIf(item -> item.getId().equals(cartItemId));

        // Cập nhật lại tổng chi phí
        double totalCost = cart.getItems().stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
        cart.setTotalCost(totalCost);

        // Lưu giỏ hàng đã cập nhật vào Redis
        redisTemplate.opsForValue().set(cacheKey, cart, 30, TimeUnit.MINUTES);
    }

    @Transactional
    public void addItemToCart(CartItemGetVm cartItemVm) {
        Cart cart = cartRepository.findByUserId(cartItemVm.userId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(cartItemVm.userId());
                    newCart.setTotalCost(0.0);
                    return cartRepository.save(newCart);
                });

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setVehicleDto(cartItemVm.vehicle());
        cartItem.setQuantity(cartItemVm.quantity());
        cartItem.setPrice(cartItemVm.price());
        cartItem.setSubtotal(cartItemVm.quantity() * cartItemVm.price());
        cartItemRepository.save(cartItem);

        cart.setTotalCost(cart.getTotalCost() + cartItem.getSubtotal());
        cartRepository.save(cart);
    }

    public Cart getCartByUserId(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
    }

    @Transactional
    public void updateCartItem(CartItemGetVm cartItemVm) {
        // Lấy thông tin giỏ hàng theo userId
        Cart cart = cartRepository.findByUserId(cartItemVm.userId())
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        // Tìm CartItem trong danh sách của Cart
        CartItem itemToUpdate = cart.getItems().stream()
                .filter(item -> item.getVehicleDto() != null &&
                        item.getVehicleDto().getChassisNumber().equals(cartItemVm.vehicle().getChassisNumber()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found in cart"));

        // Cập nhật số lượng và tổng phụ
        itemToUpdate.setQuantity(cartItemVm.quantity());
        itemToUpdate.setSubtotal(cartItemVm.quantity() * itemToUpdate.getPrice());

        // Lưu CartItem đã cập nhật vào repository
        cartItemRepository.save(itemToUpdate);

        // Cập nhật tổng chi phí của giỏ hàng
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

    @Transactional
    public void checkoutCart(String userId) {
        String cacheKey = CART_CACHE_PREFIX + userId;

        // Lấy giỏ hàng từ Redis
        Cart temporaryCart = getTemporaryCart(userId);
        if (temporaryCart == null || temporaryCart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty or not found for userId: " + userId);
        }

        // Tạo giỏ hàng trong cơ sở dữ liệu
        Cart dbCart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    newCart.setTotalCost(0.0);
                    return cartRepository.save(newCart);
                });

        // Lưu từng sản phẩm vào cơ sở dữ liệu
        for (CartItem temporaryItem : temporaryCart.getItems()) {
            CartItem dbCartItem = new CartItem();
            dbCartItem.setCart(dbCart);
            dbCartItem.setVehicleDto(temporaryItem.getVehicleDto());
            dbCartItem.setQuantity(temporaryItem.getQuantity());
            dbCartItem.setPrice(temporaryItem.getPrice());
            dbCartItem.setSubtotal(temporaryItem.getSubtotal());
            cartItemRepository.save(dbCartItem);
        }

        // Cập nhật tổng chi phí cho giỏ hàng
        double totalCost = temporaryCart.getItems().stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
        dbCart.setTotalCost(totalCost);
        cartRepository.save(dbCart);

        // Xóa giỏ hàng tạm thời khỏi Redis
        redisTemplate.delete(cacheKey);
        log.info("Cart checked out and moved to database for userId: {}", userId);
    }

}
