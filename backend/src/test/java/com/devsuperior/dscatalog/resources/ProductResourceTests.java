package com.devsuperior.dscatalog.resources;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import com.devsuperior.dscatalog.tests.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductResourceTests {
    @Autowired
	private TokenUtil tokenUtil;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private ProductDTO productDTO;
    private PageImpl<ProductDTO> page;
    private String username;
    private String password;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;
        productDTO = Factory.createProductDTO();
        page = new PageImpl<>(List.of(productDTO));
        username = "maria@gmail.com";
        password = "123456";
        Mockito.when(productService.findAllPaged(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(page);
        Mockito.when(productService.findByID(existingId)).thenReturn(productDTO);
        Mockito.when(productService.findByID(nonExistingId)).thenThrow(ResourceNotFoundException.class);
        Mockito.when(productService.update(Mockito.eq(existingId), ArgumentMatchers.any())).thenReturn(productDTO);
        Mockito.when(productService.update(Mockito.eq(nonExistingId), ArgumentMatchers.any()))
                .thenThrow(ResourceNotFoundException.class);
        Mockito.doNothing().when(productService).delete(existingId);
        Mockito.doThrow(ResourceNotFoundException.class).when(productService).delete(nonExistingId);
        Mockito.doThrow(DatabaseException.class).when(productService).delete(dependentId);
        Mockito.when(productService.insert(ArgumentMatchers.any())).thenReturn(productDTO);
    }

    @Test
    public void findAllShouldReturnPage() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/products")
                .accept(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", existingId));
        result.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.name").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.description").exists());
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", nonExistingId)
            .accept(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", existingId)
            .header("Authorization", "Bearer " + accessToken)
            .content(jsonBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.name").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.description").exists());
    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", nonExistingId)
            .header("Authorization", "Bearer " + accessToken)
            .content(jsonBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", existingId)
            .header("Authorization", "Bearer " + accessToken)
            .accept(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", nonExistingId)
            .header("Authorization", "Bearer " + accessToken)    
            .accept(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deleteShouldReturnBadRequestWhenDependentId() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", dependentId)
            .header("Authorization", "Bearer " + accessToken)
            .accept(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void insertShouldReturnProductDTOCreated() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/products")
            .header("Authorization", "Bearer " + accessToken)
            .content(jsonBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isCreated());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.name").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.description").exists());
    }
}
