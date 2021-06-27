package com.devsuperior.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIT {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objMapper;
	
	private Long existingId;
	private Long nonExistingId;
	private Long countTotalProduct;
	
	@BeforeEach
	void setup() throws Exception{
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProduct = 25L;
	}
	
	@Test
	public void findAllShouldReturnsortedPageWhenSortByName() throws Exception {
		
		ResultActions result = mockMvc.perform(get("/products?page=0&size=20&sort=name,asc")
				.accept(MediaType.APPLICATION_JSON));
				
		result.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").exists())
			.andExpect(jsonPath("$.totalElements").value(countTotalProduct))
			.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"))
			.andExpect(jsonPath("$.content[1].name").value("PC Gamer"))
			.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));		
	}
	
	@Test
	public void updateShouldProductDTOWhenIdExists() throws Exception {
		
		ProductDTO productDto = Factory.createProductDTO();
		String json = objMapper.writeValueAsString(productDto);
		
		String expectedName = productDto.getName();
		String expectedDescription = productDto.getDescription();
		
		ResultActions result = mockMvc.perform(put("/products/{id}", existingId)
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(existingId))
		.andExpect(jsonPath("$.name").value(expectedName))
		.andExpect(jsonPath("$.description").value(expectedDescription));
	}
	
	@Test
	public void updateShouldNotFoundWhenIdDoesNotExist() throws Exception {
		
		ProductDTO productDto = Factory.createProductDTO();
		String json = objMapper.writeValueAsString(productDto);
		
		
		ResultActions result = mockMvc.perform(put("/products/{id}", nonExistingId)
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
}
