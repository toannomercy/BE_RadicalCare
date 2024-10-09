package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.Product;
import lombok.Builder;

@Builder
public record ProductPostVm(String name, String description, String brand) {
    public Product toProduct (){
        Product product = new Product();
        product.setName(this.name);
        product.setDescription(this.description);
        product.setBrand(this.brand);
        return product;
    }
}
