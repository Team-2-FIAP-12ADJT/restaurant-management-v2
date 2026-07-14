package com.fiap.restaurant_management_v2.infrastructure.web;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fiap.restaurant_management_v2.IntegrationTestBase;
import com.fiap.restaurant_management_v2.infrastructure.persistence.UserEntity;
import com.fiap.restaurant_management_v2.infrastructure.persistence.UserJpaRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

class UserUpdatePasswordApiIT extends IntegrationTestBase {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserJpaRepository userJpaRepository;

    private MockMvc mockMvc;
    private PasswordEncoder passwordEncoder;

    private UUID adminId;
    private UUID donoId;
    private UUID userId;
    private String adminPassword = "Admin@123";
    private String donoPassword = "Dono@123";
    private String userPassword = "User@123";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .defaultRequest(get("/").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_DONO"))))
            .build();
        userJpaRepository.deleteAll();

        passwordEncoder = new BCryptPasswordEncoder();

        // Create test users
        adminId = UUID.randomUUID();
        donoId = UUID.randomUUID();
        userId = UUID.randomUUID();

        UserEntity admin = UserEntity.builder()
            .id(adminId)
            .name("Admin")
            .email("admin@example.com")
            .login("admin")
            .taxIdentifier("11111111111")
            .password(passwordEncoder.encode(adminPassword))
            .build();

        UserEntity dono = UserEntity.builder()
            .id(donoId)
            .name("Dono")
            .email("dono@example.com")
            .login("dono")
            .taxIdentifier("22222222222")
            .password(passwordEncoder.encode(donoPassword))
            .build();

        UserEntity user = UserEntity.builder()
            .id(userId)
            .name("User")
            .email("user@example.com")
            .login("user")
            .taxIdentifier("33333333333")
            .password(passwordEncoder.encode(userPassword))
            .build();

        userJpaRepository.save(admin);
        userJpaRepository.save(dono);
        userJpaRepository.save(user);
    }

    @Test
    @DisplayName("PATCH /users/{id}/password with correct old password returns 204")
    void updatePasswordSuccessfully() throws Exception {
        String newPassword = "NewPass456";

        mockMvc
            .perform(
                patch(ApiPaths.USERS + "/" + adminId + "/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"oldPassword\":\"" + adminPassword + "\",\"newPassword\":\"" + newPassword + "\"}"
                    )
                    .with(jwt()
                        .jwt(j -> j.subject(adminId.toString()))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
            )
            .andExpect(status().isNoContent());

        UserEntity updated = userJpaRepository.findById(adminId).get();
        assertTrue(passwordEncoder.matches(newPassword, updated.getPassword()));
        assertNotEquals(adminPassword, updated.getPassword());
    }

    @Test
    @DisplayName("PATCH /users/{id}/password with incorrect old password returns 400")
    void rejectsIncorrectOldPassword() throws Exception {
        mockMvc
            .perform(
                patch(ApiPaths.USERS + "/" + adminId + "/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"oldPassword\":\"WrongPassword\",\"newPassword\":\"NewPass456\"}"
                    )
                    .with(jwt()
                        .jwt(j -> j.subject(adminId.toString()))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /users/{id}/password with same new password returns 409")
    void rejectsSamePassword() throws Exception {
        mockMvc
            .perform(
                patch(ApiPaths.USERS + "/" + adminId + "/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"oldPassword\":\"" + adminPassword + "\",\"newPassword\":\"" + adminPassword + "\"}"
                    )
                    .with(jwt()
                        .jwt(j -> j.subject(adminId.toString()))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
            )
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PATCH /users/{id}/password without authorization returns 403")
    void rejectsUnauthorizedUser() throws Exception {
        mockMvc
            .perform(
                patch(ApiPaths.USERS + "/" + adminId + "/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"oldPassword\":\"" + adminPassword + "\",\"newPassword\":\"NewPass456\"}"
                    )
                    .with(jwt()
                        .jwt(j -> j.subject(userId.toString()))
                        .authorities(new SimpleGrantedAuthority("ROLE_USER")))
            )
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH /users/{id}/password with ownership (self) returns 204")
    void updatePasswordWithOwnership() throws Exception {
        String newPassword = "NewPass456";

        mockMvc
            .perform(
                patch(ApiPaths.USERS + "/" + donoId + "/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"oldPassword\":\"" + donoPassword + "\",\"newPassword\":\"" + newPassword + "\"}"
                    )
                    .with(jwt()
                        .jwt(j -> j.subject(donoId.toString()))
                        .authorities(new SimpleGrantedAuthority("ROLE_DONO")))
            )
            .andExpect(status().isNoContent());

        UserEntity updated = userJpaRepository.findById(donoId).get();
        assertTrue(passwordEncoder.matches(newPassword, updated.getPassword()));
    }


    @Test
    @DisplayName("PATCH /users/{id}/password without token returns 401")
    void rejectsUnauthenticatedRequest() throws Exception {
        var mockMvcNoAuth = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();

        mockMvcNoAuth
            .perform(
                patch(ApiPaths.USERS + "/" + adminId + "/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"oldPassword\":\"" + adminPassword + "\",\"newPassword\":\"NewPass456\"}"
                    )
            )
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /users/{id}/password with invalid body returns 400")
    void rejectsInvalidBody() throws Exception {
        mockMvc
            .perform(
                patch(ApiPaths.USERS + "/" + adminId + "/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}")
                    .with(jwt()
                        .jwt(j -> j.subject(adminId.toString()))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /users/{id}/password with null oldPassword returns 400")
    void rejectsNullOldPassword() throws Exception {
        mockMvc
            .perform(
                patch(ApiPaths.USERS + "/" + adminId + "/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"oldPassword\":null,\"newPassword\":\"NewPass456\"}")
                    .with(jwt()
                        .jwt(j -> j.subject(adminId.toString()))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /users/{id}/password by ADMIN for another user returns 204")
    void adminCanUpdateAnotherUserPassword() throws Exception {
        String newPassword = "NewPass456";

        mockMvc
            .perform(
                patch(ApiPaths.USERS + "/" + donoId + "/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"oldPassword\":\"" + donoPassword + "\",\"newPassword\":\"" + newPassword + "\"}"
                    )
                    .with(jwt()
                        .jwt(j -> j.subject(adminId.toString()))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
            )
            .andExpect(status().isNoContent());

        UserEntity updated = userJpaRepository.findById(donoId).get();
        assertTrue(passwordEncoder.matches(newPassword, updated.getPassword()));
    }
}
