package com.radical.be_radicalcare.Repositories;

import com.radical.be_radicalcare.Entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProductRepository extends
        JpaRepository<Product, Long>,
        PagingAndSortingRepository<Product, Long> {

}
