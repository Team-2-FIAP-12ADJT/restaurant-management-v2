package com.fiap.restaurant_management_v2.infrastructure.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fiap.restaurant_management_v2.IntegrationTestBase;
import com.fiap.restaurant_management_v2.infrastructure.persistence.MenuItemEntity;
import com.fiap.restaurant_management_v2.infrastructure.persistence.MenuItemJpaRepository;
import com.fiap.restaurant_management_v2.infrastructure.persistence.RestaurantEntity;
import com.fiap.restaurant_management_v2.infrastructure.persistence.RestaurantJpaRepository;
import com.fiap.restaurant_management_v2.infrastructure.persistence.UserEntity;
import com.fiap.restaurant_management_v2.infrastructure.persistence.UserJpaRepository;
import com.fiap.restaurant_management_v2.infrastructure.persistence.UserTypeEntity;
import com.fiap.restaurant_management_v2.infrastructure.persistence.UserTypeJpaRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

class RestaurantApiIT extends IntegrationTestBase {

    private static final String RESTAURANT_BODY_TEMPLATE = """
        {"name":"%s","address":"%s","taxIdentifier":"%s","cuisineType":"%s","openingHours":"%s","ownerId":"%s"}""";
    private static final String INVALID_BODY = """
        {"name":"","address":"","taxIdentifier":"","cuisineType":"","openingHours":"","ownerId":""}""";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RestaurantJpaRepository restaurantJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private UserTypeJpaRepository userTypeJpaRepository;

    @Autowired
    private MenuItemJpaRepository menuItemJpaRepository;

    private MockMvc mockMvc;
    private UUID ownerId;
    private UUID donoTypeId = UUID.fromString("ad854890-a521-46bb-9323-9ee4f03930cb");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .defaultRequest(get("/").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_DONO"))))
            .build();
        restaurantJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
        userTypeJpaRepository.deleteAll();

        // Criar o UserType "Dono"
        userTypeJpaRepository.save(
            UserTypeEntity.builder()
                .id(donoTypeId)
                .userType("Dono")
                .build()
        );

        ownerId = UUID.randomUUID();
        var donoType = userTypeJpaRepository.findById(donoTypeId).orElseThrow();
        userJpaRepository.save(
            UserEntity.builder()
                .id(ownerId)
                .name("Owner")
                .email("owner@mail.com")
                .login("owner")
                .taxIdentifier("12345678901")
                .password("secret")
                .userTypeEntity(donoType)
                .build()
        );
    }

    private RestaurantEntity.RestaurantEntityBuilder restaurant(
        String name,
        String cnpj
    ) {
        return RestaurantEntity.builder()
            .id(UUID.randomUUID())
            .name(name)
            .address("Rua A, 123")
            .taxIdentifier(cnpj)
            .cuisineType("Pizza")
            .openingHours("08:00-22:00")
            .owner(userJpaRepository.findById(ownerId).orElseThrow())
            .createdAt(Instant.now())
            .updatedAt(Instant.now());
    }

    private String validBody() {
        return RESTAURANT_BODY_TEMPLATE.formatted(
            "Pizza Place",
            "Rua A, 123",
            "12345678000199",
            "Pizza",
            "08:00-22:00",
            ownerId
        );
    }

    @Test
    @DisplayName("GET /api/v1/restaurants retorna lista vazia (200)")
    void listEmpty() throws Exception {
        mockMvc
            .perform(get(ApiPaths.RESTAURANTS))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content").isEmpty())
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("GET /api/v1/restaurants retorna restaurantes com owner completo (200)")
    void listWithData() throws Exception {
        restaurantJpaRepository.save(
            restaurant("Pizza Place", "11111111000101").build()
        );
        restaurantJpaRepository.save(
            restaurant("Sushi Bar", "22222222000102").build()
        );

        mockMvc
            .perform(get(ApiPaths.RESTAURANTS))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.content[0].owner.id").value(ownerId.toString()));
    }

    @Test
    @DisplayName("GET /api/v1/restaurants filtra por nome")
    void listFilterByName() throws Exception {
        restaurantJpaRepository.save(
            restaurant("Pizza Place", "11111111000101").build()
        );
        restaurantJpaRepository.save(
            restaurant("Sushi Bar", "22222222000102").build()
        );

        mockMvc
            .perform(get(ApiPaths.RESTAURANTS + "?name=Pizza"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].name").value("Pizza Place"));
    }

    @Test
    @DisplayName("GET /api/v1/restaurants/{id} retorna restaurante com owner (200)")
    void getByIdSuccess() throws Exception {
        var entity = restaurant("Pizza Place", "11111111000101").build();
        restaurantJpaRepository.save(entity);

        mockMvc
            .perform(get(ApiPaths.RESTAURANTS + "/" + entity.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(entity.getId().toString()))
            .andExpect(jsonPath("$.name").value("Pizza Place"))
            .andExpect(jsonPath("$.cuisineType").value("Pizza"))
            .andExpect(jsonPath("$.taxIdentifier").value("11.111.111/0001-01"))
            .andExpect(jsonPath("$.owner.id").value(ownerId.toString()))
            .andExpect(jsonPath("$.owner.taxIdentifier").value("123.456.789-01"));
    }

    @Test
    @DisplayName("GET /api/v1/restaurants/{id} retorna 404 quando não encontrado")
    void getByIdNotFound() throws Exception {
        mockMvc
            .perform(get(ApiPaths.RESTAURANTS + "/" + UUID.randomUUID()))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/restaurants cria restaurante (201) com owner completo")
    void createSuccess() throws Exception {
        mockMvc
            .perform(
                post(ApiPaths.RESTAURANTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(validBody())
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Pizza Place"))
            .andExpect(jsonPath("$.taxIdentifier").value("12.345.678/0001-99"))
            .andExpect(jsonPath("$.owner.id").value(ownerId.toString()));
    }

    @Test
    @DisplayName("POST /api/v1/restaurants com body inválido retorna 400")
    void createInvalid() throws Exception {
        mockMvc
            .perform(
                post(ApiPaths.RESTAURANTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(INVALID_BODY)
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/restaurants com owner inexistente retorna 404")
    void createWithNonExistentOwner() throws Exception {
        var body = RESTAURANT_BODY_TEMPLATE.formatted(
            "Pizza Place",
            "Rua A, 123",
            "12345678000199",
            "Pizza",
            "08:00-22:00",
            UUID.randomUUID()
        );

        mockMvc
            .perform(
                post(ApiPaths.RESTAURANTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
            )
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/v1/restaurants/{id} atualiza restaurante (200)")
    void updateSuccess() throws Exception {
        var entity = restaurant("Pizza Place", "11111111000101").build();
        restaurantJpaRepository.save(entity);

        var updateBody = RESTAURANT_BODY_TEMPLATE.formatted(
            "Pizza Place Updated",
            "Rua C, 789",
            "12345678000199",
            "Italiana",
            "09:00-23:00",
            ownerId
        );

        mockMvc
            .perform(
                put(ApiPaths.RESTAURANTS + "/" + entity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateBody)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Pizza Place Updated"))
            .andExpect(jsonPath("$.address").value("Rua C, 789"))
            .andExpect(jsonPath("$.cuisineType").value("Italiana"))
            .andExpect(jsonPath("$.taxIdentifier").value("12.345.678/0001-99"))
            .andExpect(jsonPath("$.owner.id").value(ownerId.toString()));
    }

    @Test
    @DisplayName("PUT /api/v1/restaurants/{id} retorna 404 quando não encontrado")
    void updateNotFound() throws Exception {
        mockMvc
            .perform(
                put(ApiPaths.RESTAURANTS + "/" + UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(validBody())
            )
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST com owner tipo Cliente retorna 422")
    void createWithClientOwnerReturns422() throws Exception {
        // Criar user do tipo Cliente
        UUID clientTypeId = UUID.fromString("bc652941-b632-47cc-8414-0ff5f14841da");
        userTypeJpaRepository.save(
            UserTypeEntity.builder()
                .id(clientTypeId)
                .userType("Cliente")
                .build()
        );
        UUID clientId = UUID.randomUUID();
        var clientType = userTypeJpaRepository.findById(clientTypeId).orElseThrow();
        userJpaRepository.save(
            UserEntity.builder()
                .id(clientId)
                .name("Client")
                .email("client@mail.com")
                .login("client")
                .taxIdentifier("11111111111")
                .password("secret")
                .userTypeEntity(clientType)
                .build()
        );

        var body = RESTAURANT_BODY_TEMPLATE.formatted(
            "Pizza Place",
            "Rua A, 123",
            "12345678000199",
            "Pizza",
            "08:00-22:00",
            clientId
        );

        mockMvc
            .perform(
                post(ApiPaths.RESTAURANTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
            )
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("POST com owner sem userType retorna 422")
    void createWithNullUserTypeReturns422() throws Exception {
        UUID nullTypeOwnerId = UUID.randomUUID();
        userJpaRepository.save(
            UserEntity.builder()
                .id(nullTypeOwnerId)
                .name("No Type User")
                .email("notype@mail.com")
                .login("notype")
                .taxIdentifier("99999999999")
                .password("secret")
                .userTypeEntity(null)
                .build()
        );

        var body = RESTAURANT_BODY_TEMPLATE.formatted(
            "Pizza Place",
            "Rua A, 123",
            "12345678000199",
            "Pizza",
            "08:00-22:00",
            nullTypeOwnerId
        );

        mockMvc
            .perform(
                post(ApiPaths.RESTAURANTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
            )
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("PUT com owner tipo Cliente retorna 422")
    void updateWithClientOwnerReturns422() throws Exception {
        var entity = restaurant("Pizza Place", "11111111000101").build();
        restaurantJpaRepository.save(entity);

        // Criar user do tipo Cliente
        UUID clientTypeId = UUID.fromString("bc652941-b632-47cc-8414-0ff5f14841da");
        UUID clientId = UUID.randomUUID();
        var clientType = userTypeJpaRepository.findById(clientTypeId).orElse(
            userTypeJpaRepository.save(
                UserTypeEntity.builder()
                    .id(clientTypeId)
                    .userType("Cliente")
                    .build()
            )
        );
        userJpaRepository.save(
            UserEntity.builder()
                .id(clientId)
                .name("Client")
                .email("client2@mail.com")
                .login("client2")
                .taxIdentifier("22222222222")
                .password("secret")
                .userTypeEntity(clientType)
                .build()
        );

        var updateBody = RESTAURANT_BODY_TEMPLATE.formatted(
            "Pizza Place",
            "Rua C, 789",
            "12345678000199",
            "Italiana",
            "09:00-23:00",
            clientId
        );

        mockMvc
            .perform(
                put(ApiPaths.RESTAURANTS + "/" + entity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateBody)
            )
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("DELETE com MenuItems ativos retorna 409")
    void deleteWithActiveMenuItemsReturns409() throws Exception {
        var entity = restaurant("Pizza Place", "11111111000101").build();
        restaurantJpaRepository.save(entity);

        // Criar MenuItem ativo
        menuItemJpaRepository.save(
            MenuItemEntity.builder()
                .id(UUID.randomUUID())
                .name("Margherita")
                .description("Classic pizza")
                .price(BigDecimal.valueOf(25.50))
                .onlyLocal(false)
                .photoPath("https://example.com/pizza.jpg")
                .restaurant(entity)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build()
        );

        mockMvc
            .perform(delete(ApiPaths.RESTAURANTS + "/" + entity.getId()))
            .andExpect(status().isConflict());

        // Verificar que restaurante NÃO foi deletado
        mockMvc
            .perform(get(ApiPaths.RESTAURANTS + "/" + entity.getId()))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/v1/restaurants/{id} exclui logicamente (204)")
    void deleteSuccess() throws Exception {
        var entity = restaurant("Pizza Place", "11111111000101").build();
        restaurantJpaRepository.save(entity);

        mockMvc
            .perform(delete(ApiPaths.RESTAURANTS + "/" + entity.getId()))
            .andExpect(status().isNoContent());

        mockMvc
            .perform(get(ApiPaths.RESTAURANTS + "/" + entity.getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/restaurants/{id} retorna 404 quando não encontrado")
    void deleteNotFound() throws Exception {
        mockMvc
            .perform(delete(ApiPaths.RESTAURANTS + "/" + UUID.randomUUID()))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/restaurants/{id} com UUID inválido retorna 400")
    void deleteInvalidUuid() throws Exception {
        mockMvc
            .perform(delete(ApiPaths.RESTAURANTS + "/invalido"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("existsByIdAndDeletedAtIsNull retorna false para restaurante excluído")
    void existsByIdReturnsFalseForDeleted() {
        var entity = restaurant("Pizza Place", "11111111000101").build();
        var id = entity.getId();
        restaurantJpaRepository.saveAndFlush(entity);

        assertTrue(restaurantJpaRepository.existsByIdAndDeletedAtIsNull(id));

        entity.setDeletedAt(Instant.now());
        restaurantJpaRepository.saveAndFlush(entity);

        assertFalse(restaurantJpaRepository.existsByIdAndDeletedAtIsNull(id));
    }

    @Test
    @DisplayName("Restaurantes excluídos logicamente não aparecem na listagem")
    void deletedRestaurantsNotListed() throws Exception {
        var entity = restaurant("Pizza Place", "11111111000101")
            .deletedAt(Instant.now())
            .build();
        restaurantJpaRepository.save(entity);

        mockMvc
            .perform(get(ApiPaths.RESTAURANTS))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content").isEmpty());

        mockMvc
            .perform(get(ApiPaths.RESTAURANTS + "/" + entity.getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST soft-delete restaurante; recriar mesmo CNPJ retorna 201")
    void reuseDeletedRestaurantCnpj() throws Exception {
        var entity = restaurant("Pizza Place", "11111111000101").build();
        restaurantJpaRepository.save(entity);
        UUID restaurantId = entity.getId();

        // Deletar restaurante
        mockMvc
            .perform(delete(ApiPaths.RESTAURANTS + "/" + restaurantId))
            .andExpect(status().isNoContent());

        // Recriar com mesmo CNPJ → 201 (não 409)
        var body = RESTAURANT_BODY_TEMPLATE.formatted(
            "Pizza Place New",
            "Rua A, 123",
            "11111111000101",
            "Pizza",
            "08:00-22:00",
            ownerId
        );

        mockMvc
            .perform(
                post(ApiPaths.RESTAURANTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());
    }
}
