package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.Category;
import com.radical.be_radicalcare.Entities.WarrantyInfo;
import lombok.Builder;

@Builder
public record CategoryPostVm(String name, Long warrantyInfoId) {
    public Category toCategory() {
        Category category = new Category();
        category.setCategoryName(this.name);
        category.setIsDeleted(false);

        WarrantyInfo warrantyInfo = new WarrantyInfo();
        warrantyInfo.setId(this.warrantyInfoId);
        category.setWarrantyInfo(warrantyInfo);

        return category;
    }
}