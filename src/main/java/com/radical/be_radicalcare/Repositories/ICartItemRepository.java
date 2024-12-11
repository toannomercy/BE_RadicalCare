package com.radical.be_radicalcare.Repositories;

import com.radical.be_radicalcare.Entities.Cart;
import com.radical.be_radicalcare.Entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ICartItemRepository extends JpaRepository<CartItem, String> {
    @Query("SELECT ci FROM CartItem ci LEFT JOIN FETCH ci.cart c LEFT JOIN FETCH ci.vehicle v WHERE c.id = :cartId")
    List<CartItem> findAllByCartId(@Param("cartId") String cartId);

    @Query("SELECT ci FROM CartItem ci LEFT JOIN FETCH ci.cart c LEFT JOIN FETCH ci.vehicle v " +
            "WHERE c.id = :cartId AND v.chassisNumber = :vehicleId")
    Optional<CartItem> findByCartAndVehicle(@Param("cartId") String cartId, @Param("vehicleId") String vehicleId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart = :cart")
    List<CartItem> findAllByCart(@Param("cart") Cart cart);

    void deleteAllByCart(Cart cart);
}