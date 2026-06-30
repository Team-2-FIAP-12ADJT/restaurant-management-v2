package com.fiap.restaurant_management_v2.infrastructure.web;

import com.fiap.restaurant_management_v2.IntegrationTestBase;
import com.fiap.restaurant_management_v2.infrastructure.persistence.RestaurantEntity;
import com.fiap.restaurant_management_v2.infrastructure.persistence.RestaurantJpaRepository;
import com.fiap.restaurant_management_v2.infrastructure.persistence.UserEntity;
import com.fiap.restaurant_management_v2.infrastructure.persistence.UserJpaRepository;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RestaurantApiIT extends IntegrationTestBase {

    private static final String RESTAURANT_BODY_TEMPLATE = """
        {"name":"%s","address":"%s","cuisineType":"%s","openingHours":"%s","ownerId":"%s"}""";
    private static final String INVALID_BODY = """
        {"name":"","address":"","cuisineType":"","openingHours":"","ownerId":""}""";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RestaurantJpaRepository restaurantJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    private MockMvc mockMvc;
    private UUID ownerId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        restaurantJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
        ownerId = UUID.randomUUID();
        userJpaRepository.save(UserEntity.builder()
                .id(ownerId)
                .name("Owner")
                .email("owner@mail.com")
                .login("owner")
                .password("secret")
                .build());
    }

    private String validBody() {
        return RESTAURANT_BODY_TEMPLATE.formatted(
                "Pizza Place", "Rua A, 123", "Pizza", "08:00-22:00", ownerId);
    }

    @Test
    @DisplayName("GET /api/v1/restaurants retorna lista vazia (200)")
    void listEmpty() throws Exception {
        mockMvc.perform(get(ApiPaths.RESTAURANTS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("GET /api/v1/restaurants retorna restaurantes cadastrados (200)")
    void listWithData() throws Exception {
        restaurantJpaRepository.save(RestaurantEntity.builder()
                .id(UUID.randomUUID())
                .name("Pizza Place")
                .address("Rua A, 123")
                .cuisineType("Pizza")
                .openingHours("08:00-22:00")
                .owner(userJpaRepository.findById(ownerId).orElseThrow())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());
        restaurantJpaRepository.save(RestaurantEntity.builder()
                .id(UUID.randomUUID())
                .name("Sushi Bar")
                .address("Rua B, 456")
                .cuisineType("Sushi")
                .openingHours("10:00-23:00")
                .owner(userJpaRepository.findById(ownerId).orElseThrow())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        mockMvc.perform(get(ApiPaths.RESTAURANTS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("GET /api/v1/restaurants filtra por nome")
    void listFilterByName() throws Exception {
        restaurantJpaRepository.save(RestaurantEntity.builder()
                .id(UUID.randomUUID())
                .name("Pizza Place")
                .address("Rua A, 123")
                .cuisineType("Pizza")
                .openingHours("08:00-22:00")
                .owner(userJpaRepository.findById(ownerId).orElseThrow())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());
        restaurantJpaRepository.save(RestaurantEntity.builder()
                .id(UUID.randomUUID())
                .name("Sushi Bar")
                .address("Rua B, 456")
                .cuisineType("Sushi")
                .openingHours("10:00-23:00")
                .owner(userJpaRepository.findById(ownerId).orElseThrow())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        mockMvc.perform(get(ApiPaths.RESTAURANTS + "?name=Pizza"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Pizza Place"));
    }

    @Test
    @DisplayName("GET /api/v1/restaurants/{id} retorna restaurante (200)")
    void getByIdSuccess() throws Exception {
        var id = UUID.randomUUID();
        restaurantJpaRepository.save(RestaurantEntity.builder()
                .id(id)
                .name("Pizza Place")
                .address("Rua A, 123")
                .cuisineType("Pizza")
                .openingHours("08:00-22:00")
                .owner(userJpaRepository.findById(ownerId).orElseThrow())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        mockMvc.perform(get(ApiPaths.RESTAURANTS + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Pizza Place"))
                .andExpect(jsonPath("$.address").value("Rua A, 123"))
                .andExpect(jsonPath("$.cuisineType").value("Pizza"))
                .andExpect(jsonPath("$.openingHours").value("08:00-22:00"));
    }

    @Test
    @DisplayName("GET /api/v1/restaurants/{id} retorna 404 quando não encontrado")
    void getByIdNotFound() throws Exception {
        mockMvc.perform(get(ApiPaths.RESTAURANTS + "/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/restaurants cria restaurante (201)")
    void createSuccess() throws Exception {
        mockMvc.perform(post(ApiPaths.RESTAURANTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Pizza Place"))
                .andExpect(jsonPath("$.address").value("Rua A, 123"))
                .andExpect(jsonPath("$.cuisineType").value("Pizza"))
                .andExpect(jsonPath("$.openingHours").value("08:00-22:00"));
    }

    @Test
    @DisplayName("POST /api/v1/restaurants com body inválido retorna 400")
    void createInvalid() throws Exception {
        mockMvc.perform(post(ApiPaths.RESTAURANTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_BODY))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/restaurants com owner inexistente retorna 409")
    void createWithNonExistentOwner() throws Exception {
        var body = RESTAURANT_BODY_TEMPLATE.formatted(
                "Pizza Place", "Rua A, 123", "Pizza", "08:00-22:00", UUID.randomUUID());

        mockMvc.perform(post(ApiPaths.RESTAURANTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PUT /api/v1/restaurants/{id} atualiza restaurante (200)")
    void updateSuccess() throws Exception {
        var id = UUID.randomUUID();
        restaurantJpaRepository.save(RestaurantEntity.builder()
                .id(id)
                .name("Pizza Place")
                .address("Rua A, 123")
                .cuisineType("Pizza")
                .openingHours("08:00-22:00")
                .owner(userJpaRepository.findById(ownerId).orElseThrow())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        var updateBody = RESTAURANT_BODY_TEMPLATE.formatted(
                "Pizza Place Updated", "Rua C, 789", "Italiana", "09:00-23:00", ownerId);

        mockMvc.perform(put(ApiPaths.RESTAURANTS + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pizza Place Updated"))
                .andExpect(jsonPath("$.address").value("Rua C, 789"))
                .andExpect(jsonPath("$.cuisineType").value("Italiana"))
                .andExpect(jsonPath("$.openingHours").value("09:00-23:00"));
    }

    @Test
    @DisplayName("PUT /api/v1/restaurants/{id} retorna 404 quando não encontrado")
    void updateNotFound() throws Exception {
        mockMvc.perform(put(ApiPaths.RESTAURANTS + "/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/restaurants/{id} exclui logicamente (204)")
    void deleteSuccess() throws Exception {
        var id = UUID.randomUUID();
        restaurantJpaRepository.save(RestaurantEntity.builder()
                .id(id)
                .name("Pizza Place")
                .address("Rua A, 123")
                .cuisineType("Pizza")
                .openingHours("08:00-22:00")
                .owner(userJpaRepository.findById(ownerId).orElseThrow())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        mockMvc.perform(delete(ApiPaths.RESTAURANTS + "/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(ApiPaths.RESTAURANTS + "/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/restaurants/{id} retorna 404 quando não encontrado")
    void deleteNotFound() throws Exception {
        mockMvc.perform(delete(ApiPaths.RESTAURANTS + "/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/restaurants/{id} com UUID inválido retorna 400")
    void deleteInvalidUuid() throws Exception {
        mockMvc.perform(delete(ApiPaths.RESTAURANTS + "/invalido"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("existsByIdAndDeletedAtIsNull retorna false para restaurante excluído")
    void existsByIdReturnsFalseForDeleted() {
        var id = UUID.randomUUID();
        var user = userJpaRepository.findById(ownerId).orElseThrow();
        restaurantJpaRepository.saveAndFlush(RestaurantEntity.builder()
                .id(id)
                .name("Pizza Place")
                .address("Rua A, 123")
                .cuisineType("Pizza")
                .openingHours("08:00-22:00")
                .owner(user)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        assertTrue(restaurantJpaRepository.existsByIdAndDeletedAtIsNull(id));

        restaurantJpaRepository.saveAndFlush(RestaurantEntity.builder()
                .id(id)
                .name("Pizza Place")
                .address("Rua A, 123")
                .cuisineType("Pizza")
                .openingHours("08:00-22:00")
                .owner(user)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .deletedAt(Instant.now())
                .build());

        assertFalse(restaurantJpaRepository.existsByIdAndDeletedAtIsNull(
                restaurantJpaRepository.findById(id).orElseThrow().getId()));
    }

    @Test
    @DisplayName("Restaurantes excluídos logicamente não aparecem na listagem")
    void deletedRestaurantsNotListed() throws Exception {
        var id = UUID.randomUUID();
        var user = userJpaRepository.findById(ownerId).orElseThrow();
        restaurantJpaRepository.save(RestaurantEntity.builder()
                .id(id)
                .name("Pizza Place")
                .address("Rua A, 123")
                .cuisineType("Pizza")
                .openingHours("08:00-22:00")
                .owner(user)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .deletedAt(Instant.now())
                .build());

        mockMvc.perform(get(ApiPaths.RESTAURANTS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty());

        mockMvc.perform(get(ApiPaths.RESTAURANTS + "/" + id))
                .andExpect(status().isNotFound());
    }
}
