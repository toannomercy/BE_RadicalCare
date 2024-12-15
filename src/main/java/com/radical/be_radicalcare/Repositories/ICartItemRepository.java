package com.radical.be_radicalcare.Repositories;

import com.radical.be_radicalcare.Entities.Cart;
import com.radical.be_radicalcare.Entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICartItemRepository extends JpaRepository<CartItem, String> {
}
