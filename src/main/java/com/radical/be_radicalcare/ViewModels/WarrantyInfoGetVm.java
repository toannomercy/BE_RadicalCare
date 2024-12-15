package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.WarrantyInfo;
import lombok.Builder;

@Builder
public record WarrantyInfoGetVm(Long id, String warrantyType, String warrantyDescription) {
    public static WarrantyInfoGetVm from(WarrantyInfo warrantyInfo) {
        return WarrantyInfoGetVm.builder()
                .id(warrantyInfo.getId())
                .warrantyType(warrantyInfo.getWarrantyType())
                .warrantyDescription(warrantyInfo.getWarrantyDescription())
                .build();
    }
}