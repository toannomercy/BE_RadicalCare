package com.radical.be_radicalcare.Repositories;

import com.radical.be_radicalcare.Entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProductRepository extends JpaRepository<Product, Long> {
}
