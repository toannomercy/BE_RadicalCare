package com.radical.be_radicalcare.Services;


import com.radical.be_radicalcare.Entities.Category;
import com.radical.be_radicalcare.Entities.CostTable;
import com.radical.be_radicalcare.Entities.Product;
import com.radical.be_radicalcare.Entities.WarrantyInfo;
import com.radical.be_radicalcare.Repositories.ICostTableRepository;
import com.radical.be_radicalcare.Repositories.IProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
@RequiredArgsConstructor
public class ProductService {
    private final ICostTableRepository costTableRepository;
    private final IProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long productId) {

        return productRepository.findById(productId);
    }

    public void addProduct(Product product) {
        productRepository.save(product);
    }

    public void updateProduct(Product product) {
        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setBrand(product.getBrand());

        CostTable costTable = costTableRepository.findById(product.getCostTable().getCostId())
                .orElseThrow(() -> new RuntimeException("CostTable not found"));
        existingProduct.setCostTable(costTable);

        productRepository.save(existingProduct);
    }

    public void deleteProductById(Long productId) {
        productRepository.deleteById(productId);
    }
}
