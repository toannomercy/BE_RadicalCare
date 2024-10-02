package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.Category;
import lombok.Builder;

@Builder
public record CategoryGetVm(Long id, String name) {
    public static CategoryGetVm from(Category category) {
        return CategoryGetVm.builder()
                .id(category.getId())
                .name(category.getCategoryName())
                .build();
    }
}
