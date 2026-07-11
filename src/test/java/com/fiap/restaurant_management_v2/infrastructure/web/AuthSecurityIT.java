package com.fiap.restaurant_management_v2.infrastructure.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fiap.restaurant_management_v2.IntegrationTestBase;
import com.fiap.restaurant_management_v2.infrastructure.persistence.UserEntity;
import com.fiap.restaurant_management_v2.infrastructure.persistence.UserJpaRepository;
import com.fiap.restaurant_management_v2.infrastructure.persistence.UserTypeEntity;
import com.jayway.jsonpath.JsonPath;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

class AuthSecurityIT extends IntegrationTestBase {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserJpaRepository userJpaRepository;

    private MockMvc mockMvc;

    private static final String ADMIN_TYPE_ID = "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d";
    private static final String ADMIN_HASH = "$2y$10$o7zgsUSyRbF3EogmG2WNbuhPNLTgHVJJmCrd0bUz.dFy8I5/tJSsW";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        userJpaRepository.deleteAll();
    }

    private void saveUser(String login, String hash, String typeId, boolean deleted) {
        var builder = UserEntity.builder()
            .id(UUID.randomUUID())
            .name("Admin")
            .email(login + "-" + UUID.randomUUID() + "@ex.com")
            .login(login)
            .taxIdentifier(String.valueOf(System.nanoTime()).substring(0, 11))
            .password(hash);
        if (typeId != null) {
            builder.userTypeEntity(UserTypeEntity.builder().id(UUID.fromString(typeId)).build());
        }
        if (deleted) {
            builder.deletedAt(Instant.now());
        }
        userJpaRepository.save(builder.build());
    }

    private String loginBody(String login, String password) {
        return "{\"login\":\"" + login + "\",\"password\":\"" + password + "\"}";
    }

    @Test
    void loginSuccessReturnsTokenAndAuthorizesAdminRoute() throws Exception {
        saveUser("admin", ADMIN_HASH, ADMIN_TYPE_ID, false);
        MvcResult res = mockMvc.perform(
            post(ApiPaths.AUTH_LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody("admin", "Senh@1234"))
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andReturn();
        String token = JsonPath.read(res.getResponse().getContentAsString(), "$.accessToken");
        mockMvc.perform(get(ApiPaths.USERS).header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    void loginWrongPasswordReturns401() throws Exception {
        saveUser("admin", ADMIN_HASH, ADMIN_TYPE_ID, false);
        mockMvc.perform(
            post(ApiPaths.AUTH_LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody("admin", "errada"))
        )
            .andExpect(status().isUnauthorized());
    }

    @Test
    void loginSoftDeletedReturns401() throws Exception {
        saveUser("admin", ADMIN_HASH, ADMIN_TYPE_ID, true);
        mockMvc.perform(
            post(ApiPaths.AUTH_LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody("admin", "Senh@1234"))
        )
            .andExpect(status().isUnauthorized());
    }

    @Test
    void loginUserWithoutTypeGetsTokenForbiddenOnAdminRoute() throws Exception {
        saveUser("admin", ADMIN_HASH, null, false);
        MvcResult res = mockMvc.perform(
            post(ApiPaths.AUTH_LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody("admin", "Senh@1234"))
        )
            .andExpect(status().isOk())
            .andReturn();
        String token = JsonPath.read(res.getResponse().getContentAsString(), "$.accessToken");
        mockMvc.perform(get(ApiPaths.USERS).header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden());
    }

    @Test
    void protectedRouteWithoutTokenReturns401() throws Exception {
        mockMvc.perform(get(ApiPaths.USERS))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void donoTokenStillAuthorizedOnAdminRoute() throws Exception {
        mockMvc.perform(
            get(ApiPaths.USERS).with(jwt().authorities(new SimpleGrantedAuthority("ROLE_DONO")))
        )
            .andExpect(status().isOk());
    }

    @Test
    void clienteTokenForbiddenOnAdminRoute() throws Exception {
        mockMvc.perform(
            get(ApiPaths.USERS).with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CLIENTE")))
        )
            .andExpect(status().isForbidden());
    }
}
