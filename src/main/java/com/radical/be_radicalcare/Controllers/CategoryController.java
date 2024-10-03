package com.radical.be_radicalcare.Controllers;

import com.radical.be_radicalcare.Services.CategoryService;
import com.radical.be_radicalcare.ViewModels.CategoryGetVm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/category")
    public ResponseEntity<List<CategoryGetVm>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories()
                .stream()
                .map(CategoryGetVm::from)
                .toList());
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<CategoryGetVm> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id)
                .map(CategoryGetVm::from)
                .orElse(null));
    }
}
