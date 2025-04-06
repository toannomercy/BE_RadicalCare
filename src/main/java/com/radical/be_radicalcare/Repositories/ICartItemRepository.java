package com.radical.be_radicalcare.Repositories;

import com.radical.be_radicalcare.Entities.Cart;
import com.radical.be_radicalcare.Entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICartItemRepository extends JpaRepository<CartItem, String> {

    // Tìm tất cả CartItem theo Cart ID
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId")
    List<CartItem> findAllByCartId(@Param("cartId") String cartId);

    // Tìm tất cả CartItem theo Cart
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart = :cart")
    List<CartItem> findAllByCart(@Param("cart") Cart cart);

    // Xóa tất cả CartItem theo Cart
    void deleteAllByCart(Cart cart);
}
