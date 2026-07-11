package com.fiap.restaurant_management_v2;

import com.fiap.restaurant_management_v2.infrastructure.persistence.MenuItemJpaRepository;
import com.fiap.restaurant_management_v2.infrastructure.persistence.RestaurantJpaRepository;
import com.fiap.restaurant_management_v2.infrastructure.persistence.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
public abstract class IntegrationTestBase {

    @ServiceConnection
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer(
        "postgres:18-alpine"
    );

    static {
        POSTGRES.start();
    }

    // Production requires JWT_SECRET from the environment (no fallback in
    // application.yaml). Tests supply an ephemeral key so the context boots.
    @DynamicPropertySource
    static void jwtProperties(DynamicPropertyRegistry registry) {
        registry.add(
            "jwt.secret",
            () -> "ZGV2LW9ubHktc2VjcmV0LTMyLWJ5dGVzLW1pbi1wYWRkaW5nMDE="
        );
    }

    @Autowired
    private MenuItemJpaRepository menuItemJpaRepository;

    @Autowired
    private RestaurantJpaRepository restaurantJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    /**
     * Container é singleton compartilhado entre TODAS as classes IT — o estado
     * acumula entre elas. Limpa em ordem FK-safe (menu_items → restaurants → users)
     * antes de cada teste para não poluir outras classes (ex.: menu item bloqueia o
     * delete de restaurant pela FK `fk_menu_items_restaurant_id`, restaurante ativo
     * bloqueia o delete de user por FK `fk_restaurants_owner_id`). Roda ANTES do setUp
     * da subclasse (JUnit: @BeforeEach da superclasse primeiro).
     */
    @BeforeEach
    void cleanSharedDatabase() {
        menuItemJpaRepository.deleteAll();
        restaurantJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }
}
