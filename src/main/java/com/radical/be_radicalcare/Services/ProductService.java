package com.radical.be_radicalcare.Services;


import com.radical.be_radicalcare.Entities.*;
import com.radical.be_radicalcare.Repositories.ICostTableRepository;
import com.radical.be_radicalcare.Repositories.IProductImageRepository;
import com.radical.be_radicalcare.Repositories.IProductRepository;
import com.radical.be_radicalcare.ViewModels.ProductPostVm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
@RequiredArgsConstructor
public class ProductService {
    private final ICostTableRepository costTableRepository;
    private final IProductRepository productRepository;
    private final CloudinaryService cloudinaryService;
    private final IProductImageRepository productImageRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long productId) {

        return productRepository.findById(productId);
    }

    public void addProduct(Product product, List<MultipartFile> images) throws IOException {
        Product savedProduct = productRepository.save(product);

        if (product.getCostTable() != null && product.getCostTable().getCostId() != null) {
            CostTable costTable = costTableRepository.findById(product.getCostTable().getCostId()).orElse(null);
            product.setCostTable(costTable);
        }

        for (MultipartFile image : images) {
            Map uploadResult = cloudinaryService.upload(image);
            String imageUrl = (String) uploadResult.get("url");

            ProductImage productImage = new ProductImage();
            productImage.setImageUrl(imageUrl);
            productImage.setProductId(savedProduct);
            productImageRepository.save(productImage);
        }
    }

    public void updateProduct(Long productId, ProductPostVm productPostVm, List<MultipartFile> newImages) throws IOException {
        Product existingProduct = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        existingProduct.setName(productPostVm.name());
        existingProduct.setDescription(productPostVm.description());
        existingProduct.setBrand(productPostVm.brand());

        if (productPostVm.costId() != null) {
            CostTable costTable = costTableRepository.findById(productPostVm.costId()).orElse(null);
            existingProduct.setCostTable(costTable);
        }

        List<ProductImage> existingImages = productImageRepository.findByProductId(existingProduct.getId());

        for (ProductImage existingImage : existingImages) {
            cloudinaryService.delete(existingImage.getImageUrl());
            productImageRepository.delete(existingImage);
        }

        for (MultipartFile newImage : newImages) {
            Map uploadResult = cloudinaryService.upload(newImage);
            String imageUrl = (String) uploadResult.get("url");
            ProductImage productImage = new ProductImage();
            productImage.setImageUrl(imageUrl);
            productImage.setProductId(existingProduct);
            productImageRepository.save(productImage);
        }

        productRepository.save(existingProduct);
    }


    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        List<ProductImage> images = productImageRepository.findByProductId(product.getId());

        for (ProductImage image : images) {
            cloudinaryService.delete(image.getImageUrl());
            productImageRepository.delete(image);
        }

        productRepository.delete(product);
    }

}
