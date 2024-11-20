package com.radical.be_radicalcare.Controllers;

import com.radical.be_radicalcare.Entities.Category;
import com.radical.be_radicalcare.Services.CategoryService;
import com.radical.be_radicalcare.ViewModels.CategoryGetVm;
import com.radical.be_radicalcare.ViewModels.CategoryPostVm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/category")
    public ResponseEntity<?> getAllCategories() {
        List<CategoryGetVm> categories = categoryService.getAllCategories()
                .stream()
                .map(CategoryGetVm::from)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Categories retrieved successfully");
        response.put("data", categories);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/category/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(category -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", 200);
                    response.put("message", "Category retrieved successfully");
                    response.put("data", CategoryGetVm.from(category));
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", 404);
                    response.put("message", "Category not found");
                    return ResponseEntity.status(404).body(response);
                });
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/category")
    public ResponseEntity<?> createCategory(@RequestBody CategoryPostVm categoryPostVm) {
        categoryService.addCategory(categoryPostVm.toCategory());

        Map<String, Object> response = new HashMap<>();
        response.put("status", 201);
        response.put("message", "Category created successfully");

        return ResponseEntity.status(201).body(response);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/category/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody CategoryPostVm categoryPostVm) {
        try {
            Category category = categoryPostVm.toCategory();
            category.setId(id);
            categoryService.updateCategory(category);

            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Category updated successfully");

            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "An error occurred while updating the category");
            response.put("error", e.getMessage());

            return ResponseEntity.status(500).body(response);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/category/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategoryById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Category deleted successfully");

        return ResponseEntity.status(200).body(response);
    }
}