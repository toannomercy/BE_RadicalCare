package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.Supplier;
import lombok.Builder;

@Builder
public record SupplierGetVm(Long supplierId, String supplierName, String supplierAddress, String supplierPhone, String supplierEmail, Boolean isDeleted) {
    public static SupplierGetVm from(Supplier supplier) {
        return SupplierGetVm.builder()
                .supplierId(supplier.getSupplierId())
                .supplierName(supplier.getSupplierName())
                .supplierAddress(supplier.getSupplierAddress())
                .supplierPhone(supplier.getSupplierPhone())
                .supplierEmail(supplier.getSupplierEmail())
                .isDeleted(supplier.getIsDeleted())
                .build();
    }
}
