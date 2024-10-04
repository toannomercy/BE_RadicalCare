package com.radical.be_radicalcare.ViewModels;


import com.radical.be_radicalcare.Entities.Product;
import lombok.Builder;

@Builder
public record ProductGetVm(Long id, String name, String description, String brand, Long CostId) {
    public static ProductGetVm from (Product product){
        return ProductGetVm.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .brand(product.getBrand())
                .CostId(product.getCostTable() != null ? product.getCostTable().getCostId() : null)
                .build();
    }
}
