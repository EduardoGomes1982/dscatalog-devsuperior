package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {
    @Autowired
    private ProductRepository repository;

    private long existingId;
    private long nonExistingId;
    private long countTotalProducts;

    @BeforeEach
    void setUp() {
        existingId = 4L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        repository.deleteById(existingId);
        Optional<Product> result = repository.findById(existingId);
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
        Product product = Factory.createProduct();
        product.setId(null);
        Product productPersisted = repository.save(product);
        Assertions.assertNotNull(productPersisted.getId());
        Assertions.assertEquals(productPersisted.getId(), product.getId());
        Assertions.assertEquals(productPersisted.getName(), product.getName());
        Assertions.assertEquals(productPersisted.getPrice(), product.getPrice());
        Assertions.assertEquals(productPersisted.getDescription(), product.getDescription());
        Assertions.assertEquals(productPersisted.getDate(), product.getDate());
        Assertions.assertEquals(productPersisted.getImgUrl(), product.getImgUrl());
        Assertions.assertEquals(countTotalProducts + 1, product.getId());
    }

    @Test
    public void saveShouldPersistWithDataUpdateWhenIdDoesNotNull() {
        Product product = Factory.createProduct();
        Product productPersisted = repository.save(product);
        Assertions.assertNotNull(productPersisted.getId());
        Assertions.assertEquals(productPersisted.getId(), product.getId());
        Assertions.assertEquals(productPersisted.getName(), product.getName());
        Assertions.assertEquals(productPersisted.getPrice(), product.getPrice());
        Assertions.assertEquals(productPersisted.getDescription(), product.getDescription());
        Assertions.assertEquals(productPersisted.getDate(), product.getDate());
        Assertions.assertEquals(productPersisted.getImgUrl(), product.getImgUrl());
    }

    @Test
    public void findByIdShouldReturnOptionalWhenIdExist() {
        Optional<Product> product = repository.findById(existingId);
        Assertions.assertTrue(product.isPresent());
    }

    @Test
    public void findByIdShouldReturnEmptyOptionalWhenIdDoesNotExist() {
        Optional<Product> product = repository.findById(nonExistingId);
        Assertions.assertFalse(product.isPresent());
    }
}
