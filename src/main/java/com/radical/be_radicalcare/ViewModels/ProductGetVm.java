package com.radical.be_radicalcare.ViewModels;


import com.radical.be_radicalcare.Entities.Product;
import com.radical.be_radicalcare.Entities.ProductImage;
import lombok.Builder;

import java.util.List;

@Builder
public record ProductGetVm(Long id, String name, String description, String brand, Long costId, List<String> imageUrls) {
    public static ProductGetVm from(Product product) {
        return ProductGetVm.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .brand(product.getBrand())
                .costId(product.getCostTable() != null ? product.getCostTable().getCostId() : null)
                .imageUrls(product.getProductImages() != null ?
                        product.getProductImages().stream()
                                .map(ProductImage::getImageUrl)
                                .toList() : null)
                .build();
    }
}

