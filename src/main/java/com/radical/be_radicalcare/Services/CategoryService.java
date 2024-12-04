package com.radical.be_radicalcare.Services;

import com.radical.be_radicalcare.Entities.Category;
import com.radical.be_radicalcare.Entities.WarrantyInfo;
import com.radical.be_radicalcare.Repositories.ICategoryRepository;
import com.radical.be_radicalcare.Repositories.IWarrantyInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
public class CategoryService {

    private final ICategoryRepository categoryRepository;
    private final IWarrantyInfoRepository warrantyInfoRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId);
    }

    public void addCategory(Category category) {
        categoryRepository.save(category);
    }

    public void updateCategory(Category category) {
        Category existingCategory = categoryRepository.findById(category.getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        existingCategory.setCategoryName(category.getCategoryName());

        WarrantyInfo warrantyInfo = warrantyInfoRepository.findById(category.getWarrantyInfo().getId())
                .orElseThrow(() -> new RuntimeException("WarrantyInfo not found"));
        existingCategory.setWarrantyInfo(warrantyInfo);

        categoryRepository.save(existingCategory);
    }
    public List<Long> getCategoryIdsByName(String name) {
        return categoryRepository.findByCategoryNameContainingIgnoreCase(name)
                .stream()
                .map(Category::getId)
                .collect(Collectors.toList());
    }
    public void deleteCategoryById(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}
