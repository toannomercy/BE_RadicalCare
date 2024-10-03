package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.Category;
import lombok.Builder;

@Builder
public record CategoryPostVm(String name) {
    public Category toCategory() {
        Category category = new Category();
        category.setCategoryName(this.name);
        return category;
    }
}
