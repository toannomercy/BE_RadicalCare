package com.radical.be_radicalcare.Controllers;

import com.radical.be_radicalcare.Entities.Supplier;
import com.radical.be_radicalcare.Services.SupplierService;
import com.radical.be_radicalcare.ViewModels.SupplierGetVm;
import com.radical.be_radicalcare.ViewModels.SupplierPostVm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierService supplierService;

    @GetMapping("/supplier")
    public ResponseEntity<?> getAllSuppliers() {
        List<SupplierGetVm> suppliers = supplierService.getAllSuppliers()
                .stream()
                .map(SupplierGetVm::from)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Suppliers retrieved successfully");
        response.put("data", suppliers);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/supplier/{id}")
    public ResponseEntity<?> getSupplierById(@PathVariable Long id) {
        return supplierService.getSupplierById(id)
                .map(supplier -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", 200);
                    response.put("message", "Supplier retrieved successfully");
                    response.put("data", SupplierGetVm.from(supplier));
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", 404);
                    response.put("message", "Supplier not found");
                    return ResponseEntity.status(404).body(response);
                });
    }

    @PostMapping("/supplier")
    public ResponseEntity<?> createSupplier(@RequestBody SupplierPostVm supplierPostVm) {
        Supplier supplier = supplierPostVm.toSupplier();
        supplierService.addSupplier(supplier);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 201);
        response.put("message", "Supplier created successfully");

        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/supplier/{id}")
    public ResponseEntity<?> updateSupplier(@PathVariable Long id, @RequestBody SupplierPostVm supplierPostVm) {
        Supplier supplier = supplierPostVm.toSupplier();
        supplier.setSupplierId(id);
        supplierService.updateSupplier(supplier);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Supplier updated successfully");

        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/supplier/{id}")
    public ResponseEntity<?> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplierById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Supplier deleted successfully");

        return ResponseEntity.status(200).body(response);
    }

}
