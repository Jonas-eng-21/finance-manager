package com.financialmanajer.financial.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financialmanajer.financial.application.dto.CreateCategoryDTO;
import com.financialmanajer.financial.application.usecase.CreateCategoryUseCase;
import com.financialmanajer.financial.domain.exception.DomainValidationException;
import com.financialmanajer.financial.domain.model.Category;
import com.financialmanajer.financial.presentation.dto.CreateCategoryRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CategoryController.class)
@WithMockUser
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CreateCategoryUseCase createCategoryUseCase;

    @Test
    @DisplayName("Deve retornar 201 Created e a categoria quando a requisição for válida")
    void should_return_201_when_request_is_valid() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest("Alimentação");
        Category mockCategory = new Category(1L, "Alimentação");
        mockCategory.setId(100L);

        when(createCategoryUseCase.execute(any(CreateCategoryDTO.class))).thenReturn(mockCategory);

        mockMvc.perform(post("/api/categories")
                        .with(csrf())
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.name").value("Alimentação"));
    }

    @Test
    @DisplayName("Deve retornar erro de validação (400) quando o nome for vazio")
    void should_return_400_when_name_is_empty() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest("");

        mockMvc.perform(post("/api/categories")
                        .with(csrf())
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 409 Conflict quando a categoria já existe")
    void should_return_409_when_category_already_exists() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest("Lazer");

        when(createCategoryUseCase.execute(any(CreateCategoryDTO.class)))
                .thenThrow(new DomainValidationException("category.validation.name.already_exists"));

        mockMvc.perform(post("/api/categories")
                        .with(csrf())
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict()) // Esperamos 409!
                .andExpect(jsonPath("$.message").exists());
    }
}