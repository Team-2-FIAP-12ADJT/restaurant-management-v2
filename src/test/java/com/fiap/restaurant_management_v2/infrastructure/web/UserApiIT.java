package com.fiap.restaurant_management_v2.infrastructure.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fiap.restaurant_management_v2.IntegrationTestBase;
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

class UserApiIT extends IntegrationTestBase {

    private static final String VALID_BODY = """
        {"name":"Ada","email":"ada@example.com","login":"ada","taxIdentifier":"12345678901","password":"secret123"}""";
    private static final String INVALID_BODY = """
        {"name":"","email":"not-an-email","login":"ada","taxIdentifier":"12345678901","password":"secret123"}""";

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
    @DisplayName(
        "POST /api/v1/users cria usuário (201) e persiste senha como hash BCrypt"
    )
    void createsUser() throws Exception {
        mockMvc
            .perform(
                post(ApiPaths.USERS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(VALID_BODY)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.email").value("ada@example.com"))
            .andExpect(jsonPath("$.taxIdentifier").value("123.456.789-01"))
            .andExpect(jsonPath("$.password").doesNotExist());

        UserEntity persisted = userJpaRepository.findAll().get(0);
        assertNotEquals("secret123", persisted.getPassword());
        assertTrue(persisted.getPassword().startsWith("$2"));
        // Armazenado cru (11 dígitos), apresentado mascarado.
        assertEquals("12345678901", persisted.getTaxIdentifier());
    }

    @Test
    @DisplayName(
        "POST com CPF mascarado salva dígitos crus e retorna formatado"
    )
    void createsUserNormalizingMaskedCpf() throws Exception {
        mockMvc
            .perform(
                post(ApiPaths.USERS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"name\":\"Ada\",\"email\":\"ada@example.com\",\"login\":\"ada\",\"taxIdentifier\":\"123.456.789-01\",\"password\":\"secret123\"}"
                    )
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.taxIdentifier").value("123.456.789-01"));

        UserEntity persisted = userJpaRepository.findAll().get(0);
        assertEquals("12345678901", persisted.getTaxIdentifier());
    }

    @Test
    @DisplayName("POST com CPF em formato inválido retorna 400")
    void rejectsInvalidCpf() throws Exception {
        mockMvc
            .perform(
                post(ApiPaths.USERS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"name\":\"Ada\",\"email\":\"ada@example.com\",\"login\":\"ada\",\"taxIdentifier\":\"123\",\"password\":\"secret123\"}"
                    )
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH mudando CPF para um já existente retorna 409")
    void patchDuplicateTaxReturns409() throws Exception {
        createAda();
        mockMvc
            .perform(
                post(ApiPaths.USERS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"name\":\"Bob\",\"email\":\"bob@example.com\",\"login\":\"bob\",\"taxIdentifier\":\"98765432100\",\"password\":\"secret123\"}"
                    )
            )
            .andExpect(status().isCreated());

        UUID bobId = userJpaRepository
            .findAll()
            .stream()
            .filter(e -> "bob".equals(e.getLogin()))
            .findFirst()
            .orElseThrow()
            .getId();

        // Bob tenta assumir o CPF da Ada (mascarado) → 409
        mockMvc
            .perform(
                patch(ApiPaths.USERS + "/" + bobId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"taxIdentifier\":\"123.456.789-01\"}")
            )
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST com email/login duplicado retorna 409")
    void rejectsDuplicate() throws Exception {
        mockMvc
            .perform(
                post(ApiPaths.USERS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(VALID_BODY)
            )
            .andExpect(status().isCreated());

        mockMvc
            .perform(
                post(ApiPaths.USERS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(VALID_BODY)
            )
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST com payload inválido retorna 400")
    void rejectsInvalidPayload() throws Exception {
        mockMvc
            .perform(
                post(ApiPaths.USERS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(INVALID_BODY)
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName(
        "POST sem taxIdentifier retorna 400 com erro por campo (não genérico)"
    )
    void rejectsMissingTaxIdentifierWithFieldError() throws Exception {
        mockMvc
            .perform(
                post(ApiPaths.USERS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"name\":\"Marcos\",\"email\":\"marq@mail.com\",\"login\":\"marcoveio\",\"password\":\"123456\"}"
                    )
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("Falha de validação"))
            .andExpect(jsonPath("$.errors.taxIdentifier").exists());
    }

    @Test
    @DisplayName("POST com CPF em formato inválido retorna 400 com erro por campo")
    void rejectsMalformedTaxIdentifierWithFieldError() throws Exception {
        mockMvc
            .perform(
                post(ApiPaths.USERS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"name\":\"Marcos\",\"email\":\"marq@mail.com\",\"login\":\"marcoveio\",\"taxIdentifier\":\"123\",\"password\":\"123456\"}"
                    )
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("Falha de validação"))
            .andExpect(jsonPath("$.errors.taxIdentifier").exists());
    }

    @Test
    @DisplayName(
        "PATCH parcial (só name) retorna 200 e preserva email/login/senha/createdAt"
    )
    void patchPartialPreservesOthers() throws Exception {
        UUID id = createAda();
        UserEntity before = userJpaRepository.findById(id).orElseThrow();
        String originalPassword = before.getPassword();
        Instant originalCreatedAt = before.getCreatedAt();

        mockMvc
            .perform(
                patch(ApiPaths.USERS + "/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"Ada Lovelace\"}")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Ada Lovelace"))
            .andExpect(jsonPath("$.email").value("ada@example.com"))
            .andExpect(jsonPath("$.login").value("ada"))
            .andExpect(jsonPath("$.password").doesNotExist());

        UserEntity after = userJpaRepository.findById(id).orElseThrow();
        assertEquals("Ada Lovelace", after.getName());
        assertEquals("ada@example.com", after.getEmail());
        assertEquals("ada", after.getLogin());
        assertEquals(originalPassword, after.getPassword());
        assertEquals(originalCreatedAt, after.getCreatedAt());
    }

    @Test
    @DisplayName("PATCH com corpo vazio retorna 200 e não altera nada")
    void patchEmptyBodyIsNoOp() throws Exception {
        UUID id = createAda();

        mockMvc
            .perform(
                patch(ApiPaths.USERS + "/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Ada"))
            .andExpect(jsonPath("$.email").value("ada@example.com"));
    }

    @Test
    @DisplayName("PATCH em usuário inexistente retorna 404")
    void patchNonExistentReturns404() throws Exception {
        mockMvc
            .perform(
                patch(ApiPaths.USERS + "/" + UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"Nope\"}")
            )
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH mudando email para um já existente retorna 409")
    void patchDuplicateEmailReturns409() throws Exception {
        createAda();
        mockMvc
            .perform(
                post(ApiPaths.USERS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"name\":\"Bob\",\"email\":\"bob@example.com\",\"login\":\"bob\",\"taxIdentifier\":\"98765432100\",\"password\":\"secret123\"}"
                    )
            )
            .andExpect(status().isCreated());

        UUID bobId = userJpaRepository
            .findAll()
            .stream()
            .filter(e -> "bob".equals(e.getLogin()))
            .findFirst()
            .orElseThrow()
            .getId();

        mockMvc
            .perform(
                patch(ApiPaths.USERS + "/" + bobId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"ada@example.com\"}")
            )
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PATCH login só com espaços retorna 400 (não vira no-op)")
    void patchBlankLoginReturns400() throws Exception {
        UUID id = createAda();

        mockMvc
            .perform(
                patch(ApiPaths.USERS + "/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"login\":\"   \"}")
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName(
        "PATCH email duplicado com case diferente retorna 409 (normalização)"
    )
    void patchEmailCaseInsensitiveDuplicateReturns409() throws Exception {
        createAda();
        mockMvc
            .perform(
                post(ApiPaths.USERS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"name\":\"Bob\",\"email\":\"bob@example.com\",\"login\":\"bob\",\"taxIdentifier\":\"98765432100\",\"password\":\"secret123\"}"
                    )
            )
            .andExpect(status().isCreated());

        UUID bobId = userJpaRepository
            .findAll()
            .stream()
            .filter(e -> "bob".equals(e.getLogin()))
            .findFirst()
            .orElseThrow()
            .getId();

        mockMvc
            .perform(
                patch(ApiPaths.USERS + "/" + bobId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"ADA@EXAMPLE.COM\"}")
            )
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PATCH em usuário soft-deleted retorna 404")
    void patchSoftDeletedReturns404() throws Exception {
        UUID id = createAda();

        mockMvc
            .perform(delete(ApiPaths.USERS + "/" + id))
            .andExpect(status().isNoContent());

        mockMvc
            .perform(
                patch(ApiPaths.USERS + "/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"Ghost\"}")
            )
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName(
        "PATCH mudando CPF (mascarado) persiste dígitos crus e retorna formatado"
    )
    void patchChangesTaxPersistsAndFormats() throws Exception {
        UUID id = createAda();

        mockMvc
            .perform(
                patch(ApiPaths.USERS + "/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"taxIdentifier\":\"987.654.321-00\"}")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.taxIdentifier").value("987.654.321-00"));

        UserEntity after = userJpaRepository.findById(id).orElseThrow();
        assertEquals("98765432100", after.getTaxIdentifier());
    }

    private UUID createAda() throws Exception {
        mockMvc
            .perform(
                post(ApiPaths.USERS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(VALID_BODY)
            )
            .andExpect(status().isCreated());
        return userJpaRepository
            .findAll()
            .stream()
            .filter(e -> "ada".equals(e.getLogin()))
            .findFirst()
            .orElseThrow()
            .getId();
    }
}
