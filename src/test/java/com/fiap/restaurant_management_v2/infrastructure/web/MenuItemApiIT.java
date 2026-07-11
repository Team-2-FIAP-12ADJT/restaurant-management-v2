package com.fiap.restaurant_management_v2.infrastructure.web;

import com.fiap.restaurant_management_v2.IntegrationTestBase;
import com.fiap.restaurant_management_v2.domain.MenuItem;
import com.fiap.restaurant_management_v2.infrastructure.persistence.MenuItemEntity;
import com.fiap.restaurant_management_v2.infrastructure.persistence.MenuItemJpaRepository;
import com.fiap.restaurant_management_v2.infrastructure.persistence.RestaurantEntity;
import com.fiap.restaurant_management_v2.infrastructure.persistence.RestaurantJpaRepository;
import com.fiap.restaurant_management_v2.infrastructure.persistence.UserEntity;
import com.fiap.restaurant_management_v2.infrastructure.persistence.UserJpaRepository;
import com.jayway.jsonpath.JsonPath;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MenuItemApiIT extends IntegrationTestBase {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MenuItemJpaRepository menuItemJpaRepository;

    @Autowired
    private RestaurantJpaRepository restaurantJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    private MockMvc mockMvc;
    private UUID restaurantId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .defaultRequest(get("/").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_DONO"))))
            .build();
        menuItemJpaRepository.deleteAll();
        restaurantJpaRepository.deleteAll();
        userJpaRepository.deleteAll();

        UserEntity owner = userJpaRepository.save(
            UserEntity.builder()
                .id(UUID.randomUUID())
                .name("Ada")
                .email("ada-" + UUID.randomUUID() + "@example.com")
                .login("ada-" + UUID.randomUUID())
                    .taxIdentifier("12345678901")
                .password("hash")
                .build()
        );

        restaurantId = UUID.randomUUID();
        Instant now = Instant.now();
        restaurantJpaRepository.save(
            RestaurantEntity.builder()
                .id(restaurantId)
                .name("Cantina")
                .address("Rua A")
                .taxIdentifier("12345678000195")
                .cuisineType("Italiana")
                .openingHours("11h-23h")
                .owner(owner)
                .createdAt(now)
                .updatedAt(now)
                .build()
        );
    }

    @Test
    @DisplayName("CRUD de menu item funciona de ponta a ponta")
    void executesCrudFlow() throws Exception {
        MvcResult createResult = mockMvc
            .perform(
                post(ApiPaths.MENU_ITEMS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createBody(restaurantId))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Risoto"))
            .andExpect(jsonPath("$.price").value(39.90))
            .andExpect(jsonPath("$.onlyLocal").value(true))
            .andExpect(
                jsonPath("$.restaurantId").value(restaurantId.toString())
            )
            .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        UUID id = UUID.fromString(JsonPath.read(responseBody, "$.id"));

        mockMvc.perform(get(ApiPaths.MENU_ITEMS + "/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Risoto"));

        mockMvc.perform(get(ApiPaths.MENU_ITEMS).param("name", "soto"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.content[0].id").value(id.toString()));

        mockMvc
            .perform(
                get(
                    ApiPaths.MENU_ITEMS
                        + "/restaurant/"
                        + restaurantId
                )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(
                jsonPath("$.content[0].restaurantId")
                    .value(restaurantId.toString())
            );

        mockMvc
            .perform(
                put(ApiPaths.MENU_ITEMS + "/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateBody(restaurantId))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.name").value("Risoto especial"))
            .andExpect(jsonPath("$.price").value(49.90))
            .andExpect(jsonPath("$.onlyLocal").value(false));

        mockMvc.perform(delete(ApiPaths.MENU_ITEMS + "/" + id))
            .andExpect(status().isNoContent());

        mockMvc.perform(get(ApiPaths.MENU_ITEMS + "/" + id))
            .andExpect(status().isNotFound());

        mockMvc.perform(get(ApiPaths.MENU_ITEMS))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(0))
            .andExpect(jsonPath("$.content").isEmpty());

        mockMvc
            .perform(
                get(
                    ApiPaths.MENU_ITEMS
                        + "/restaurant/"
                        + restaurantId
                )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(0))
            .andExpect(jsonPath("$.content").isEmpty());

        MenuItemEntity deleted = menuItemJpaRepository.findById(id).orElseThrow();
        assertNotNull(deleted.getDeletedAt());
    }

    @Test
    @DisplayName("Payload inválido retorna 400")
    void rejectsInvalidPayload() throws Exception {
        String body = """
            {
              "name": "",
              "description": "",
              "price": 0,
              "onlyLocal": null,
              "photoPath": "",
              "restaurantId": null
            }
            """;

        mockMvc
            .perform(
                post(ApiPaths.MENU_ITEMS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Campos maiores que os limites retornam 400")
    void rejectsOversizedFields() throws Exception {
        MvcResult createResult = mockMvc
            .perform(
                post(ApiPaths.MENU_ITEMS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createBody(restaurantId))
            )
            .andExpect(status().isCreated())
            .andReturn();
        UUID id = UUID.fromString(
            JsonPath.read(createResult.getResponse().getContentAsString(), "$.id")
        );

        String body = """
            {
              "name": "%s",
              "description": "%s",
              "price": 39.90,
              "onlyLocal": true,
              "photoPath": "%s",
              "restaurantId": "%s"
            }
            """.formatted(
                "N".repeat(MenuItem.MAX_NAME_LENGTH + 1),
                "D".repeat(MenuItem.MAX_DESCRIPTION_LENGTH + 1),
                "P".repeat(MenuItem.MAX_PHOTO_PATH_LENGTH + 1),
                restaurantId
            );

        mockMvc
            .perform(
                post(ApiPaths.MENU_ITEMS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
            )
            .andExpect(status().isBadRequest());

        mockMvc
            .perform(
                put(ApiPaths.MENU_ITEMS + "/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Restaurante inexistente retorna 404")
    void rejectsMissingRestaurant() throws Exception {
        mockMvc
            .perform(
                post(ApiPaths.MENU_ITEMS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createBody(UUID.randomUUID()))
            )
            .andExpect(status().isNotFound());
    }

    private static String createBody(UUID restaurantId) {
        return """
            {
              "name": "Risoto",
              "description": "Risoto de cogumelos",
              "price": 39.90,
              "onlyLocal": true,
              "photoPath": "/images/risoto.jpg",
              "restaurantId": "%s"
            }
            """.formatted(restaurantId);
    }

    private static String updateBody(UUID restaurantId) {
        return """
            {
              "name": "Risoto especial",
              "description": "Risoto de cogumelos frescos",
              "price": 49.90,
              "onlyLocal": false,
              "photoPath": "/images/risoto-especial.jpg",
              "restaurantId": "%s"
            }
            """.formatted(restaurantId);
    }
}
