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

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional
public class ProductServiceIT {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private long existingId;
    private long nonDependetExistingId;
    private long nonExistingId;
    private long countTotalProduct;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonDependetExistingId = 4L;
        nonExistingId = 30L;
        countTotalProduct = 25L;
    }

    @Test
    public void deleteShouldDeleteResourceWhenIdDependentExists() {
        productService.delete(existingId);
        Assertions.assertEquals(countTotalProduct - 1, productRepository.count());
    }

    @Test
    public void deleteShouldDeleteResourceWhenIdExists() {
        productService.delete(nonDependetExistingId);
        Assertions.assertEquals(countTotalProduct - 1, productRepository.count());
    }

    @Test
    public void deleteShouldDeleteResourceWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.delete(nonExistingId);
        });
    }

    @Test
    public void findAllPageShouldReturnPageWhenPageExists() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<ProductDTO> result = productService.findAllPaged(0L, "", pageRequest);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(0, result.getNumber());
        Assertions.assertEquals(10, result.getSize());
        Assertions.assertEquals(countTotalProduct, result.getTotalElements());
    }

    @Test
    public void findAllPageShouldReturnEmptyPageWhenPageDoesNotExist() {
        PageRequest pageRequest = PageRequest.of(5, 10);
        Page<ProductDTO> result = productService.findAllPaged(0L, "", pageRequest);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void findAllPageShouldReturnSortedPageWhenSortByName() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));
        Page<ProductDTO> result = productService.findAllPaged(0L, "", pageRequest);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(0, result.getNumber());
        Assertions.assertEquals(10, result.getSize());
        Assertions.assertEquals(countTotalProduct, result.getTotalElements());
        Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
        Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
        Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
    }
}
