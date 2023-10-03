package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {
    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private Category category;
    private CategoryDTO categoryDTO;
    private PageImpl<Category> page;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        category = Factory.createCategory();
        categoryDTO = Factory.createCategoryDTO();
        page = new PageImpl<>(List.of(category));

        Mockito.doNothing().when(categoryRepository).deleteById(existingId);
        Mockito.doThrow(EmptyResultDataAccessException.class).when(categoryRepository).deleteById(nonExistingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(categoryRepository).deleteById(dependentId);
        Mockito.when(categoryRepository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
        Mockito.when(categoryRepository.save(ArgumentMatchers.any())).thenReturn(category);
        Mockito.when(categoryRepository.findById(existingId)).thenReturn(Optional.of(category));
        Mockito.when(categoryRepository.findById(nonExistingId)).thenReturn(Optional.empty());
        Mockito.when(categoryRepository.getReferenceById(existingId)).thenReturn(category);
        Mockito.when(categoryRepository.getReferenceById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(() -> categoryService.delete(existingId));
        Mockito.verify(categoryRepository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    public void deleteShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> categoryService.delete(nonExistingId));
        Mockito.verify(categoryRepository, Mockito.times(1)).deleteById(nonExistingId);
    }

    @Test
    public void deleteShouldThrowsDatabaseExceptionWhenDependentId() {
        Assertions.assertThrows(DatabaseException.class, () -> categoryService.delete(dependentId));
        Mockito.verify(categoryRepository, Mockito.times(1)).deleteById(dependentId);
    }

    @Test
    public void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CategoryDTO> result = categoryService.findAllPaged(pageable);
        Assertions.assertNotNull(result);
        Mockito.verify(categoryRepository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() {
        CategoryDTO result = categoryService.findByID(existingId);
        Assertions.assertNotNull(result);
    }

    @Test
    public void findByIdShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> categoryService.findByID(nonExistingId));
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() {
        CategoryDTO result = categoryService.update(existingId, categoryDTO);
        Assertions.assertNotNull(result);
    }

    @Test
    public void updateShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> categoryService.update(nonExistingId, categoryDTO));
    }
}