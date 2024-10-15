package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.CostTable;
import com.radical.be_radicalcare.Entities.Product;
import lombok.Builder;

@Builder
public record ProductPostVm(String name, String description, String brand, Long costId) {
    public Product toProduct() {
        Product product = new Product();
        product.setName(this.name);
        product.setDescription(this.description);
        product.setBrand(this.brand);
        if (this.costId != null) {
            CostTable costTable = new CostTable();
            costTable.setCostId(this.costId);
            product.setCostTable(costTable);
        }
        return product;
    }
}
