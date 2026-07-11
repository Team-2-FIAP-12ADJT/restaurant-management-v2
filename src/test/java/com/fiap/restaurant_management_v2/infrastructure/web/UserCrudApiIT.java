package com.fiap.restaurant_management_v2.infrastructure.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

class UserCrudApiIT extends IntegrationTestBase {

    private static final String ADA_BODY = """
        {"name":"Ada","email":"ada@example.com","login":"ada","taxIdentifier":"12345678901","password":"secret123"}""";
    private static final String BOB_BODY = """
        {"name":"Bob","email":"bob@example.com","login":"bob","taxIdentifier":"98765432100","password":"secret123"}""";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private RestaurantJpaRepository restaurantJpaRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .defaultRequest(get("/").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_DONO"))))
            .build();
        restaurantJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/v1/users returns an empty page")
    void listEmpty() throws Exception {
        mockMvc
            .perform(get(ApiPaths.USERS))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.size").value(10))
            .andExpect(jsonPath("$.totalElements").value(0))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    @DisplayName("GET /api/v1/users lists active users without exposing passwords")
    void listWithData() throws Exception {
        createUser(ADA_BODY, "ada");
        createUser(BOB_BODY, "bob");

        mockMvc
            .perform(get(ApiPaths.USERS))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.content[0].password").doesNotExist())
            .andExpect(jsonPath("$.content[1].password").doesNotExist())
            .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("GET /api/v1/users pagina explicitamente os usuários")
    void listWithExplicitPagination() throws Exception {
        createUser(ADA_BODY, "ada");
        createUser(BOB_BODY, "bob");
        createUser(
            "{\"name\":\"Charlie\",\"email\":\"charlie@example.com\",\"login\":\"charlie\",\"taxIdentifier\":\"11122233344\",\"password\":\"secret123\"}",
            "charlie"
        );

        mockMvc
            .perform(get(ApiPaths.USERS).param("page", "2").param("size", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(2))
            .andExpect(jsonPath("$.size").value(1))
            .andExpect(jsonPath("$.totalElements").value(3))
            .andExpect(jsonPath("$.totalPages").value(3))
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].name").value("Bob"));
    }

    @Test
    @DisplayName("GET /api/v1/users filters by masked CPF")
    void listFiltersByMaskedTaxIdentifier() throws Exception {
        createUser(ADA_BODY, "ada");
        createUser(BOB_BODY, "bob");

        mockMvc
            .perform(
                get(ApiPaths.USERS).param("taxIdentifier", "987.654.321-00")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].name").value("Bob"))
            .andExpect(
                jsonPath("$.content[0].taxIdentifier").value("987.654.321-00")
            );
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} returns an active user")
    void getByIdSuccess() throws Exception {
        UUID id = createUser(ADA_BODY, "ada");

        mockMvc
            .perform(get(ApiPaths.USERS + "/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.name").value("Ada"))
            .andExpect(jsonPath("$.email").value("ada@example.com"))
            .andExpect(jsonPath("$.login").value("ada"))
            .andExpect(jsonPath("$.taxIdentifier").value("123.456.789-01"))
            .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} returns 404 when user does not exist")
    void getByIdNotFound() throws Exception {
        mockMvc
            .perform(get(ApiPaths.USERS + "/" + UUID.randomUUID()))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} retorna 400 para UUID inválido")
    void getByIdInvalidUuid() throws Exception {
        mockMvc
            .perform(get(ApiPaths.USERS + "/invalido"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{id} soft deletes an active user")
    void deleteSuccess() throws Exception {
        UUID id = createUser(ADA_BODY, "ada");

        mockMvc
            .perform(delete(ApiPaths.USERS + "/" + id))
            .andExpect(status().isNoContent());

        UserEntity deleted = userJpaRepository.findById(id).orElseThrow();
        assertNotNull(deleted.getDeletedAt());

        mockMvc
            .perform(get(ApiPaths.USERS + "/" + id))
            .andExpect(status().isNotFound());
        mockMvc
            .perform(get(ApiPaths.USERS))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(0))
            .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{id} returns 404 when user does not exist")
    void deleteNotFound() throws Exception {
        mockMvc
            .perform(delete(ApiPaths.USERS + "/" + UUID.randomUUID()))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{id} retorna 400 para UUID inválido")
    void deleteInvalidUuid() throws Exception {
        mockMvc
            .perform(delete(ApiPaths.USERS + "/invalido"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{id} bloqueia usuário com restaurante ativo")
    void deleteUserWithActiveRestaurantReturnsConflict() throws Exception {
        UUID id = createUser(ADA_BODY, "ada");
        createActiveRestaurantForOwner(id);

        mockMvc
            .perform(delete(ApiPaths.USERS + "/" + id))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.detail").value(
                "Não é possível deletar o usuário: possui restaurante ativo"
            ));

        UserEntity user = userJpaRepository.findById(id).orElseThrow();
        assertNull(user.getDeletedAt());
    }

    @Test
    @DisplayName("User CRUD end-to-end")
    void crudEndToEnd() throws Exception {
        mockMvc
            .perform(
                post(ApiPaths.USERS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ADA_BODY)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Ada"))
            .andExpect(jsonPath("$.password").doesNotExist());

        UUID id = findUserIdByLogin("ada");

        mockMvc
            .perform(get(ApiPaths.USERS + "/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.email").value("ada@example.com"));

        mockMvc
            .perform(get(ApiPaths.USERS).param("login", "ada"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].id").value(id.toString()));

        mockMvc
            .perform(
                patch(ApiPaths.USERS + "/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"name\":\"Ada Lovelace\",\"email\":\"lovelace@example.com\",\"login\":\"lovelace\",\"taxIdentifier\":\"987.654.321-00\"}"
                    )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Ada Lovelace"))
            .andExpect(jsonPath("$.email").value("lovelace@example.com"))
            .andExpect(jsonPath("$.login").value("lovelace"))
            .andExpect(jsonPath("$.taxIdentifier").value("987.654.321-00"))
            .andExpect(jsonPath("$.password").doesNotExist());

        UserEntity updated = userJpaRepository.findById(id).orElseThrow();
        assertEquals("Ada Lovelace", updated.getName());
        assertEquals("lovelace@example.com", updated.getEmail());
        assertEquals("lovelace", updated.getLogin());
        assertEquals("98765432100", updated.getTaxIdentifier());

        mockMvc
            .perform(delete(ApiPaths.USERS + "/" + id))
            .andExpect(status().isNoContent());

        assertNotNull(
            userJpaRepository.findById(id).orElseThrow().getDeletedAt()
        );
        mockMvc
            .perform(get(ApiPaths.USERS + "/" + id))
            .andExpect(status().isNotFound());
        mockMvc
            .perform(get(ApiPaths.USERS).param("login", "lovelace"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isEmpty());
    }

    private UUID createUser(String body, String login) throws Exception {
        mockMvc
            .perform(
                post(ApiPaths.USERS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
            )
            .andExpect(status().isCreated());
        return findUserIdByLogin(login);
    }

    private UUID findUserIdByLogin(String login) {
        return userJpaRepository
            .findAll()
            .stream()
            .filter(e -> login.equals(e.getLogin()))
            .findFirst()
            .orElseThrow()
            .getId();
    }

    private void createActiveRestaurantForOwner(UUID ownerId) {
        restaurantJpaRepository.save(
            RestaurantEntity.builder()
                .id(UUID.randomUUID())
                .name("Pizza Place")
                .address("Rua A, 123")
                .taxIdentifier("12345678000199")
                .cuisineType("Pizza")
                .openingHours("08:00-22:00")
                .owner(userJpaRepository.findById(ownerId).orElseThrow())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build()
        );
    }
}
