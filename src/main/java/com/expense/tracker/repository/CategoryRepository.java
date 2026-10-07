package com.expense.tracker.repository;

import com.expense.tracker.entity.Category;
import com.expense.tracker.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByType(CategoryType type);
    boolean existsByNameAndType(String name, CategoryType type);
}
