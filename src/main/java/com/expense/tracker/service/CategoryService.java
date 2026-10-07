package com.expense.tracker.service;

import com.expense.tracker.entity.Category;
import com.expense.tracker.entity.CategoryType;
import com.expense.tracker.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getCategoriesByType(String type) {
        if (type == null || type.isEmpty()) {
            return categoryRepository.findAll();
        }
        CategoryType categoryType = CategoryType.valueOf(type.toUpperCase());
        return categoryRepository.findByType(categoryType);
    }

    public Category addCategory(Category category) {
        if (categoryRepository.existsByNameAndType(category.getName(), category.getType())) {
            throw new RuntimeException("Category already exists");
        }
        // Ensure new custom categories are not set as default
        category.setDefault(false);
        category.setActive(true);
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            if (category.isDefault()) {
                throw new RuntimeException("Cannot delete default categories");
            }
            categoryRepository.deleteById(id);
        } else {
            throw new RuntimeException("Category not found with id: " + id);
        }
    }
}
