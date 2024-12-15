package com.radical.be_radicalcare.Services;

import com.radical.be_radicalcare.Entities.Supplier;
import com.radical.be_radicalcare.Repositories.ISupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
@RequiredArgsConstructor
public class SupplierService {
    private final ISupplierRepository supplierRepository;

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Optional<Supplier> getSupplierById(Long supplierId) {
        return supplierRepository.findById(supplierId);
    }

    public void addSupplier(Supplier supplier) {
        supplierRepository.save(supplier);
    }

    public void updateSupplier(Supplier supplier) {
        Supplier existingSupplier = supplierRepository.findById(supplier.getSupplierId()).orElse(null);
        assert existingSupplier != null;
        existingSupplier.setSupplierName(supplier.getSupplierName());
        existingSupplier.setSupplierAddress(supplier.getSupplierAddress());
        existingSupplier.setSupplierPhone(supplier.getSupplierPhone());
        existingSupplier.setSupplierEmail(supplier.getSupplierEmail());
        supplierRepository.save(existingSupplier);
    }

    public void deleteSupplierById(Long supplierId) {
        supplierRepository.deleteById(supplierId);
    }
}
