package com.devsuperior.dscatalog.repositories;

import java.util.List;
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
    private CategoryRepository repository;

    private long existingId;
    private long nonExistingId;
    private long countTotalCategories;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalCategories = 4L;
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        repository.deleteById(existingId);
        Optional<Category> result = repository.findById(existingId);
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
            repository.deleteById(nonExistingId);
        });
    }

    @Test
    public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
        Category category = Factory.createCategory();
        category.setId(null);
        category = repository.save(category);
        Assertions.assertNotNull(category.getId());
        Assertions.assertEquals(countTotalCategories + 1, category.getId());
    }

    @Test
    public void saveShouldPersistWithDataUpdateWhenIdDoesNotNull() {
        Category category = Factory.createCategory();
        Category categoryPersisted = repository.save(category);
        Assertions.assertNotNull(categoryPersisted.getId());
        Assertions.assertEquals(categoryPersisted.getId(), category.getId());
        Assertions.assertEquals(categoryPersisted.getName(), category.getName());
    }

    @Test
    public void findByIdShouldReturnOptionalWhenIdExist() {
        Optional<Category> category = repository.findById(existingId);
        Assertions.assertTrue(category.isPresent());
    }

    @Test
    public void findByIdShouldReturnEmptyOptionalWhenIdDoesNotExist() {
        Optional<Category> category = repository.findById(nonExistingId);
        Assertions.assertFalse(category.isPresent());
    }

    @Test
    public void findAllShouldReturnListCategories() {
        List<Category> result = repository.findAll();
        Assertions.assertEquals(countTotalCategories, result.size());
        Assertions.assertEquals(result.get(0).getName(), "Livros");
    }
}
