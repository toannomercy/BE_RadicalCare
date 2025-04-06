package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.WarrantyInfo;
import lombok.Builder;

@Builder
public record WarrantyInfoPostVm(String warrantyType, String warrantyDescription) {
    public WarrantyInfo toWarrantyInfo() {
        WarrantyInfo warrantyInfo = new WarrantyInfo();
        warrantyInfo.setWarrantyType(this.warrantyType);
        warrantyInfo.setWarrantyDescription(this.warrantyDescription);
        return warrantyInfo;
    }
}