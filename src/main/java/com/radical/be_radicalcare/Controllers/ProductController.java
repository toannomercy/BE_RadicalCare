package com.radical.be_radicalcare.Controllers;


import com.radical.be_radicalcare.Entities.Product;
import com.radical.be_radicalcare.Services.ProductService;
import com.radical.be_radicalcare.ViewModels.ProductGetVm;
import com.radical.be_radicalcare.ViewModels.ProductPostVm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProductController {
    private final ProductService productService;

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/product")
    public ResponseEntity<?> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Page<Product> productPage = productService.getAllProducts(page, size, sortBy);
        List<ProductGetVm> products = productPage
                .stream()
                .map(ProductGetVm::from)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Products retrieved successfully");
        response.put("data", products);
        response.put("currentPage", productPage.getNumber());
        response.put("totalItems", productPage.getTotalElements());
        response.put("totalPages", productPage.getTotalPages());

        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/product/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(product -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", 200);
                    response.put("message", "Category retrieved successfully");
                    response.put("data", ProductGetVm.from(product));
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", 404);
                    response.put("message", "Product not found");
                    return ResponseEntity.status(404).body(response);
                });
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(value = "/product", consumes = "multipart/form-data")
    public ResponseEntity<?> createProduct(@RequestPart("productPostVm") ProductPostVm productPostVm,
                                           @RequestPart("images") List<MultipartFile> images) {
        try {
            productService.addProduct(productPostVm.toProduct(), images);
            Map<String, Object> response = new HashMap<>();
            response.put("status", 201);
            response.put("message", "Product created successfully");

            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "An error occurred while creating the product");
            response.put("error", e.getMessage());

            return ResponseEntity.status(500).body(response);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/product/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateProduct(@PathVariable Long id,
                                           @RequestPart("productPostVm") ProductPostVm productPostVm,
                                           @RequestPart("images") List<MultipartFile> newImages) {
        try {
            productService.updateProduct(id, productPostVm, newImages);
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Product updated successfully");
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "An error occurred while updating the product");
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Product deleted successfully");

        return ResponseEntity.status(200).body(response);
    }
}
