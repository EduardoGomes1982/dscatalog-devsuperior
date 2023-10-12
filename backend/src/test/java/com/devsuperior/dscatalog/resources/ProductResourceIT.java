package com.devsuperior.dscatalog.resources;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.tests.Factory;
import com.devsuperior.dscatalog.tests.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIT {
	@Autowired
	private TokenUtil tokenUtil;

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private long existingId;
    private long nonExistingId;
    private long countTotalProduct;
    private ProductDTO productDTO;
    private String username;
    private String password;
    
    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 30L;
        countTotalProduct = 25L;
        productDTO = Factory.createProductDTO();
        username = "maria@gmail.com";
        password = "123456";
    }
    
    @Test
    public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/products?page=0&size=12&sort=name,asc")
            .accept(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(countTotalProduct));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("Macbook Pro"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content[1].name").value("PC Gamer"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content[2].name").value("PC Gamer Alfa"));
    }
    
    @Test
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", existingId)
            .header("Authorization", "Bearer " + accessToken)
            .content(jsonBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(existingId));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(productDTO.getName()));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.description").value(productDTO.getDescription()));
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
}
