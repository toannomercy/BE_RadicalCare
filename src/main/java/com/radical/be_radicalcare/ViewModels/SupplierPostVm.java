package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.Supplier;
import lombok.Builder;

@Builder
public record SupplierPostVm(String supplierName, String supplierAddress, String supplierPhone, String supplierEmail) {
    public Supplier toSupplier() {
        Supplier supplier = new Supplier();
        supplier.setSupplierName(this.supplierName);
        supplier.setSupplierAddress(this.supplierAddress);
        supplier.setSupplierPhone(this.supplierPhone);
        supplier.setSupplierEmail(this.supplierEmail);
        supplier.setIsDeleted(false);
        return supplier;
    }
}
