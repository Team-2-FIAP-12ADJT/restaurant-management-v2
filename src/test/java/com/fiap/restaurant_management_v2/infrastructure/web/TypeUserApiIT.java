package com.fiap.restaurant_management_v2.infrastructure.web;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fiap.restaurant_management_v2.IntegrationTestBase;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsGateway;
import com.fiap.restaurant_management_v2.infrastructure.persistence.UserEntity;
import com.fiap.restaurant_management_v2.infrastructure.persistence.UserJpaRepository;
import com.fiap.restaurant_management_v2.infrastructure.persistence.UserTypeEntity;
import com.fiap.restaurant_management_v2.infrastructure.persistence.UserTypeJpaRepository;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

class TypeUserApiIT extends IntegrationTestBase {

    private static final String VALID_BODY = """
        {"userType":"admin"}""";
    private static final String INVALID_BODY = """
        {"userType":""}""";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserTypeJpaRepository userTypeJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private UserTypeDsGateway userTypeDsGateway;

    @Autowired
    private UserDsGateway userDsGateway;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .defaultRequest(get("/").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_DONO"))))
            .build();
        userJpaRepository.deleteAll();
        userTypeJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/v1/users-type retorna lista vazia (200)")
    void listEmpty() throws Exception {
        mockMvc
            .perform(get(ApiPaths.USERS_TYPE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    @DisplayName("GET /api/v1/users-type retorna tipos cadastrados (200)")
    void listWithData() throws Exception {
        userTypeJpaRepository.save(
            UserTypeEntity.builder()
                .id(UUID.randomUUID())
                .userType("admin")
                .build()
        );
        userTypeJpaRepository.save(
            UserTypeEntity.builder()
                .id(UUID.randomUUID())
                .userType("waiter")
                .build()
        );

        mockMvc
            .perform(get(ApiPaths.USERS_TYPE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/v1/users-type/{id} retorna tipo (200)")
    void getByIdSuccess() throws Exception {
        var id = UUID.randomUUID();
        userTypeJpaRepository.save(
            UserTypeEntity.builder().id(id).userType("admin").build()
        );

        mockMvc
            .perform(get(ApiPaths.USERS_TYPE + "/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nameType").value("admin"));
    }

    @Test
    @DisplayName(
        "GET /api/v1/users-type/{id} retorna 404 quando não encontrado"
    )
    void getByIdNotFound() throws Exception {
        mockMvc
            .perform(get(ApiPaths.USERS_TYPE + "/" + UUID.randomUUID()))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/users-type cria tipo (201)")
    void createSuccess() throws Exception {
        mockMvc
            .perform(
                post(ApiPaths.USERS_TYPE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(VALID_BODY)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.nameType").value("admin"));
    }

    @Test
    @DisplayName("POST /api/v1/users-type com body inválido retorna 400")
    void createInvalid() throws Exception {
        mockMvc
            .perform(
                post(ApiPaths.USERS_TYPE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(INVALID_BODY)
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/users-type com nome duplicado retorna 409")
    void createDuplicate() throws Exception {
        mockMvc
            .perform(
                post(ApiPaths.USERS_TYPE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(VALID_BODY)
            )
            .andExpect(status().isCreated());

        mockMvc
            .perform(
                post(ApiPaths.USERS_TYPE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(VALID_BODY)
            )
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Banco bloqueia tipos ativos duplicados")
    void databaseRejectsDuplicateActiveUserType() {
        userTypeJpaRepository.saveAndFlush(
            UserTypeEntity.builder()
                .id(UUID.randomUUID())
                .userType("admin")
                .build()
        );

        var duplicate = UserTypeEntity.builder()
            .id(UUID.randomUUID())
            .userType("admin")
            .build();

        assertThrows(DataIntegrityViolationException.class, () ->
            userTypeJpaRepository.saveAndFlush(duplicate)
        );
    }

    @Test
    @DisplayName("Banco permite recriar tipo após exclusão lógica")
    void databaseAllowsDuplicateNameWhenExistingTypeIsDeleted() {
        userTypeJpaRepository.saveAndFlush(
            UserTypeEntity.builder()
                .id(UUID.randomUUID())
                .userType("admin")
                .deletedAt(Instant.now())
                .build()
        );

        assertDoesNotThrow(() ->
            userTypeJpaRepository.saveAndFlush(
                UserTypeEntity.builder()
                    .id(UUID.randomUUID())
                    .userType("admin")
                    .build()
            )
        );
    }

    @Test
    @DisplayName("Gateway considera duplicidade apenas para tipos ativos")
    void gatewayChecksOnlyActiveUserTypeNames() {
        userTypeJpaRepository.saveAndFlush(
            UserTypeEntity.builder()
                .id(UUID.randomUUID())
                .userType("admin")
                .deletedAt(Instant.now())
                .build()
        );

        assertFalse(userTypeDsGateway.existsByUserType("admin"));

        userTypeJpaRepository.saveAndFlush(
            UserTypeEntity.builder()
                .id(UUID.randomUUID())
                .userType("admin")
                .build()
        );

        assertTrue(userTypeDsGateway.existsByUserType("admin"));
    }

    @Test
    @DisplayName("PUT /api/v1/users-type/{id} atualiza tipo (200)")
    void updateSuccess() throws Exception {
        var id = UUID.randomUUID();
        userTypeJpaRepository.save(
            UserTypeEntity.builder().id(id).userType("admin").build()
        );

        mockMvc
            .perform(
                put(ApiPaths.USERS_TYPE + "/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"userType\":\"manager\"}")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nameType").value("manager"));
    }

    @Test
    @DisplayName(
        "PUT /api/v1/users-type/{id} retorna 404 quando não encontrado"
    )
    void updateNotFound() throws Exception {
        mockMvc
            .perform(
                put(ApiPaths.USERS_TYPE + "/" + UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(VALID_BODY)
            )
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/users-type/{id} exclui logicamente (204)")
    void deleteSuccess() throws Exception {
        var id = UUID.randomUUID();
        userTypeJpaRepository.save(
            UserTypeEntity.builder().id(id).userType("admin").build()
        );

        mockMvc
            .perform(delete(ApiPaths.USERS_TYPE + "/" + id))
            .andExpect(status().isNoContent());

        mockMvc
            .perform(get(ApiPaths.USERS_TYPE + "/" + id))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName(
        "DELETE /api/v1/users-type/{id} retorna 404 quando não encontrado"
    )
    void deleteNotFound() throws Exception {
        mockMvc
            .perform(delete(ApiPaths.USERS_TYPE + "/" + UUID.randomUUID()))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/users-type/{id} com UUID inválido retorna 400")
    void deleteInvalidUuid() throws Exception {
        mockMvc
            .perform(delete(ApiPaths.USERS_TYPE + "/invalido"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Desvincula tipo apenas de usuários ativos")
    void unbindsUserTypeOnlyFromActiveUsers() {
        var typeId = UUID.randomUUID();
        userTypeJpaRepository.saveAndFlush(
            UserTypeEntity.builder().id(typeId).userType("admin").build()
        );

        var activeUserId = UUID.randomUUID();
        var deletedUserId = UUID.randomUUID();
        userJpaRepository.save(
            UserEntity.builder()
                .id(activeUserId)
                .name("Active User")
                .email("active@mail.com")
                .login("active")
                .taxIdentifier("11111111111")
                .password("123456")
                .userTypeEntity(UserTypeEntity.builder().id(typeId).build())
                .build()
        );
        userJpaRepository.saveAndFlush(
            UserEntity.builder()
                .id(deletedUserId)
                .name("Deleted User")
                .email("deleted@mail.com")
                .login("deleted")
                .taxIdentifier("22222222222")
                .password("123456")
                .userTypeEntity(UserTypeEntity.builder().id(typeId).build())
                .deletedAt(Instant.now())
                .build()
        );

        userDsGateway.unbindUserType(typeId);

        var activeUser = userJpaRepository.findById(activeUserId).orElseThrow();
        var deletedUser = userJpaRepository
            .findById(deletedUserId)
            .orElseThrow();
        assertNull(activeUser.getUserTypeEntity());
        assertNotNull(deletedUser.getUserTypeEntity());
        assertEquals(typeId, deletedUser.getUserTypeEntity().getId());
    }
}
