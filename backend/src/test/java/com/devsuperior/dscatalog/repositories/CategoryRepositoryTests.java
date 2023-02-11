package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.tests.Factory;

@DataJpaTest
public class CategoryRepositoryTests {
    @Autowired
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private long countTotalCategories;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalCategories = 3L;
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        categoryRepository.deleteById(existingId);
        Optional<Category> result = categoryRepository.findById(existingId);
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
            categoryRepository.deleteById(nonExistingId);
        });
    }

    @Test
    public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
        Category category = Factory.createCategory();
        category.setId(null);
        category = categoryRepository.save(category);
        Assertions.assertNotNull(category.getId());
        Assertions.assertEquals(countTotalCategories + 1, category.getId());
    }

    @Test
    public void findByIdShouldReturnOptionalWhenIdExist() {
        Optional<Category> category = categoryRepository.findById(existingId);
        Assertions.assertTrue(category.isPresent());
    }

    @Test
    public void findByIdShouldReturnEmptyOptionalWhenIdDoesNotExist() {
        Optional<Category> category = categoryRepository.findById(nonExistingId);
        Assertions.assertFalse(category.isPresent());
    }
}
