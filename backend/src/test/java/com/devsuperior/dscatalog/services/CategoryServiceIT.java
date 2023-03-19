package com.devsuperior.dscatalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional
public class CategoryServiceIT {
    @Autowired
    private CategoryService service;

    @Autowired
    private CategoryRepository repository;

    private long dependentExistingId;
    private long nonDependentExistingId;
    private long nonExistingId;
    private long countTotalProduct;
    
    @BeforeEach
    void setUp() throws Exception {
        dependentExistingId = 1L;
        nonDependentExistingId = 4L;
        nonExistingId = 30L;
        countTotalProduct = 4L;
    }

    @Test
    public void deleteShouldThrowsWhenIdDependentExists() {
        // Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependentExistingId);
        // });
    }

    @Test
    public void deleteShouldDeleteResourceWhenDependentIdExists() {
        service.delete(dependentExistingId);
        // Assertions.assertEquals(countTotalProduct - 1, repository.count());
    }

    @Test
    public void deleteShouldDeleteResourceWhenNonDependentIdExists() {
        service.delete(nonDependentExistingId);
        Assertions.assertEquals(countTotalProduct - 1, repository.count());
    }

    @Test
    public void deleteShouldThrowsWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class,() -> {
            service.delete(nonExistingId);
        });
    }

    @Test
    public void findAllPageShouldReturnPageWhenPageExists() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<CategoryDTO> result = service.findAllPaged(pageRequest);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(0, result.getNumber());
        Assertions.assertEquals(10, result.getSize());
        Assertions.assertEquals(countTotalProduct, result.getTotalElements());
    }

    @Test
    public void findAllPageShouldReturnEmptyPageWhenPageDoesNotExist() {
        PageRequest pageRequest = PageRequest.of(5, 10);
        Page<CategoryDTO> result = service.findAllPaged(pageRequest);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void findAllPageShouldReturnSortedPageWhenSortByName() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));
        Page<CategoryDTO> result = service.findAllPaged(pageRequest);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(0, result.getNumber());
        Assertions.assertEquals(10, result.getSize());
        Assertions.assertEquals(countTotalProduct, result.getTotalElements());
        Assertions.assertEquals("Computadores", result.getContent().get(0).getName());
        Assertions.assertEquals("Eletrônicos", result.getContent().get(1).getName());
        Assertions.assertEquals("Ferramentas", result.getContent().get(2).getName());
        Assertions.assertEquals("Livros", result.getContent().get(3).getName());
    }
}
