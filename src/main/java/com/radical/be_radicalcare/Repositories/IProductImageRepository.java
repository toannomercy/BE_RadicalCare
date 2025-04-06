package com.radical.be_radicalcare.Repositories;

import com.radical.be_radicalcare.Entities.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductImageRepository extends JpaRepository<ProductImage, Long> {
    @Query("SELECT pi FROM ProductImage pi WHERE pi.productId.id = :productId")
    List<ProductImage> findByProductId(Long productId);
}

