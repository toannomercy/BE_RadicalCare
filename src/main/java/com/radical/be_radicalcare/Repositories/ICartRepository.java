package com.radical.be_radicalcare.Repositories;

import com.radical.be_radicalcare.Entities.Cart;
import com.radical.be_radicalcare.Entities.CartItem;
import com.radical.be_radicalcare.Entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ICartRepository extends JpaRepository<Cart, String> {
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.userId = :userId")
    Optional<Cart> findByUserId(@Param("userId") String userId);
}