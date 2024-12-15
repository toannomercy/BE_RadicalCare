package com.radical.be_radicalcare.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.radical.be_radicalcare.Dto.VehicleDto;
import com.radical.be_radicalcare.Entities.Cart;
import com.radical.be_radicalcare.Entities.CartItem;
import com.radical.be_radicalcare.Entities.Vehicle;
import com.radical.be_radicalcare.Entities.VehicleImage;
import com.radical.be_radicalcare.Repositories.ICartRepository;
import com.radical.be_radicalcare.Repositories.IVehicleRepository;
import com.radical.be_radicalcare.ViewModels.CartItemGetVm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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

        // Lấy Cart từ Redis
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

        // Lấy thông tin Vehicle từ cơ sở dữ liệu
        Vehicle vehicle = vehicleRepository.findById(chassisNumber)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with chassis number: " + chassisNumber));

        // Chuyển đổi Vehicle sang VehicleDto
        VehicleDto vehicleDto = toVehicleDto(vehicle);

        // Tạo CartItem mới
        CartItem item = new CartItem();
        item.setId(UUID.randomUUID().toString());
        item.setVehicleDto(vehicleDto); // Gán DTO thay vì thực thể Vehicle
        item.setQuantity(quantity);
        item.setPrice(vehicleDto.getPrice());
        item.setSubtotal(vehicleDto.getPrice() * quantity);
        item.setCart(cart);

        // Kiểm tra vehicleDto đã được khởi tạo đầy đủ
        if (item.getVehicleDto() == null) {
            throw new IllegalStateException("VehicleDto is null before saving to Redis");
        }
        log.info("CartItem created with VehicleDto: {}", item.getVehicleDto());


        // Thêm CartItem vào danh sách
        cart.getItems().add(item);

        // Cập nhật tổng chi phí
        double totalCost = cart.getItems().stream().mapToDouble(CartItem::getSubtotal).sum();
        cart.setTotalCost(totalCost);

        // Lưu Cart vào Redis
        log.info("Saving cart to Redis: {}", cart);
        redisTemplate.opsForValue().set(cacheKey, cart, 30, TimeUnit.MINUTES);
        log.info("Cart saved to Redis with key: {}", cacheKey);
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

//    public void removeVehicleFromTemporaryCart(String userId, String chassisNumber) {
//        String cacheKey = CART_CACHE_PREFIX + userId;
//        Cart cart = (Cart) redisTemplate.opsForValue().get(cacheKey);
//        if (cart != null) {
//            cart.getItems().removeIf(item -> item.getVehicle().getChassisNumber().equals(chassisNumber));
//            redisTemplate.opsForValue().set(cacheKey, cart, 30, TimeUnit.MINUTES);
//        }
//    }
//
//    public void removeVehicleFromPersistentCart(String userId, String chassisNumber) {
//        Cart cart = cartRepository.findByUserId(userId).orElse(null);
//        if (cart != null) {
//            cart.getItems().removeIf(item -> item.getVehicle().getChassisNumber().equals(chassisNumber));
//            cartRepository.save(cart);
//        }
//    }
}
