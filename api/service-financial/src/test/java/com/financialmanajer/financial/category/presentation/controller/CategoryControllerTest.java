package com.financialmanajer.financial.category.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financialmanajer.financial.category.application.dto.CreateCategoryDTO;
import com.financialmanajer.financial.category.application.usecase.CreateCategoryUseCase;
import com.financialmanajer.financial.category.application.usecase.DeleteCategoryUseCase;
import com.financialmanajer.financial.category.application.usecase.ListCategoriesUseCase;
import com.financialmanajer.financial.category.application.usecase.UpdateCategoryUseCase;
import com.financialmanajer.financial.shared.domain.exception.DomainValidationException;
import com.financialmanajer.financial.category.domain.model.Category;
import com.financialmanajer.financial.transaction.domain.model.TransactionType;
import com.financialmanajer.financial.category.presentation.dto.CreateCategoryRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CategoryController.class)
@WithMockUser
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ListCategoriesUseCase listCategoriesUseCase;

    @MockitoBean
    private CreateCategoryUseCase createCategoryUseCase;

    @MockitoBean
    private UpdateCategoryUseCase updateCategoryUseCase;

    @MockitoBean
    private DeleteCategoryUseCase deleteCategoryUseCase;

    @Test
    @DisplayName("Deve retornar 201 Created e a categoria quando a requisição for válida")
    void should_return_201_when_request_is_valid() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest("Alimentação" , TransactionType.EXPENSE);
        Category mockCategory = new Category(1L, "Alimentação" , TransactionType.EXPENSE);
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
        CreateCategoryRequest request = new CreateCategoryRequest("" , TransactionType.EXPENSE);

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
        CreateCategoryRequest request = new CreateCategoryRequest("Lazer" , TransactionType.EXPENSE);

        when(createCategoryUseCase.execute(any(CreateCategoryDTO.class)))
                .thenThrow(new DomainValidationException("category.validation.name.already_exists"));

        mockMvc.perform(post("/api/categories")
                        .with(csrf())
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Deve retornar 200 OK e a lista de categorias")
    void should_return_200_and_list_when_listing() throws Exception {
        Long userId = 1L;
        when(listCategoriesUseCase.execute(userId)).thenReturn(List.of(
                new Category(userId, "Alimentação" , TransactionType.EXPENSE)
        ));

        mockMvc.perform(get("/api/categories")
                        .header("X-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alimentação"));
    }

    @Test
    @DisplayName("Deve retornar 204 No Content ao editar categoria válida")
    void should_return_204_when_update_is_valid() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest("Nome Editado", TransactionType.EXPENSE);

        mockMvc.perform(put("/api/categories/100")
                        .with(csrf())
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar 204 No Content ao excluir categoria com sucesso")
    void should_return_204_when_delete_is_successful() throws Exception {
        mockMvc.perform(delete("/api/categories/100")
                        .with(csrf())
                        .header("X-User-Id", "1"))
                .andExpect(status().isNoContent());
    }
}