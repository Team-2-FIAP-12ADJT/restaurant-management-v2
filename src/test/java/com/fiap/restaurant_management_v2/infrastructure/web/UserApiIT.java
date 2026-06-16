package com.fiap.restaurant_management_v2.infrastructure.web;

import com.fiap.restaurant_management_v2.IntegrationTestBase;
import com.fiap.restaurant_management_v2.infrastructure.persistence.UserEntity;
import com.fiap.restaurant_management_v2.infrastructure.persistence.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserApiIT extends IntegrationTestBase {
    private static final String VALID_BODY = """
            {"name":"Ada","email":"ada@example.com","login":"ada","password":"secret123"}""";
    private static final String INVALID_BODY = """
            {"name":"","email":"not-an-email","login":"ada","password":"secret123"}""";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserJpaRepository userJpaRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        userJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/v1/users cria usuário (201) e persiste senha como hash BCrypt")
    void createsUser() throws Exception {
        mockMvc.perform(post(ApiPaths.USERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("ada@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        UserEntity persisted = userJpaRepository.findAll().get(0);
        assertNotEquals("secret123", persisted.getPassword());
        assertTrue(persisted.getPassword().startsWith("$2"));
    }

    @Test
    @DisplayName("POST com email/login duplicado retorna 409")
    void rejectsDuplicate() throws Exception {
        mockMvc.perform(post(ApiPaths.USERS).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isCreated());

        mockMvc.perform(post(ApiPaths.USERS).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST com payload inválido retorna 400")
    void rejectsInvalidPayload() throws Exception {
        mockMvc.perform(post(ApiPaths.USERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_BODY))
                .andExpect(status().isBadRequest());
    }
}
