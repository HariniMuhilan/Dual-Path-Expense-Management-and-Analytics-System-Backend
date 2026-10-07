package com.expense.tracker.config;

import com.expense.tracker.entity.Category;
import com.expense.tracker.entity.CategoryType;
import com.expense.tracker.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(CategoryRepository categoryRepository) {
        return args -> {
            List<String> householdCategories = Arrays.asList(
                    "Bills", "Food", "Groceries", "Travel", "Rent", "EMI", "Investment", "Salary for workers", "Miscellaneous"
            );

            List<String> businessCategories = Arrays.asList(
                    "Investments", "Profits", "Loss", "Operations", "Marketing", "Software Subscriptions", "Taxes"
            );

            // Seed Household Categories
            for (String name : householdCategories) {
                if (!categoryRepository.existsByNameAndType(name, CategoryType.HOUSEHOLD)) {
                    Category category = Category.builder()
                            .name(name)
                            .type(CategoryType.HOUSEHOLD)
                            .isDefault(true)
                            .isActive(true)
                            .build();
                    categoryRepository.save(category);
                }
            }

            // Seed Business Categories
            for (String name : businessCategories) {
                if (!categoryRepository.existsByNameAndType(name, CategoryType.BUSINESS)) {
                    Category category = Category.builder()
                            .name(name)
                            .type(CategoryType.BUSINESS)
                            .isDefault(true)
                            .isActive(true)
                            .build();
                    categoryRepository.save(category);
                }
            }
            
            System.out.println("Database seeded with default categories.");
        };
    }
}
