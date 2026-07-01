package com.fiap.restaurant_management_v2;

import com.fiap.restaurant_management_v2.infrastructure.persistence.RestaurantJpaRepository;
import com.fiap.restaurant_management_v2.infrastructure.persistence.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
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

    @Autowired
    private RestaurantJpaRepository restaurantJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    /**
     * Container é singleton compartilhado entre TODAS as classes IT — o estado
     * acumula entre elas. Limpa em ordem FK-safe (restaurants → users) antes de
     * cada teste para não poluir outras classes (ex.: restaurante ativo bloqueia
     * o delete de user por FK `fk_restaurants_owner_id`). Roda ANTES do setUp da
     * subclasse (JUnit: @BeforeEach da superclasse primeiro).
     */
    @BeforeEach
    void cleanSharedDatabase() {
        restaurantJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }
}
