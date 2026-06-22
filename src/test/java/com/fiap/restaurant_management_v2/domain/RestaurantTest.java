package com.fiap.restaurant_management_v2.domain;

import com.fiap.restaurant_management_v2.domain.exception.InvalidRestaurantException;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RestaurantTest {

    @Test
    @DisplayName("Cria restaurante com sucesso e gera id")
    void createsRestaurantSuccessfully() {
        var ownerId = UUID.randomUUID();
        Restaurant r = Restaurant.create("Foo", "Rua A", "Italiana", "Seg-Sex 11h-23h", ownerId);

        assertNotNull(r.getId());
        assertEquals("Foo", r.getName());
        assertEquals("Rua A", r.getAddress());
        assertEquals("Italiana", r.getCuisineType());
        assertEquals("Seg-Sex 11h-23h", r.getOpeningHours());
        assertEquals(ownerId, r.getOwnerId());
    }

    @Test
    @DisplayName("Proíbe nome em branco")
    void rejectsBlankName() {
        assertThrows(InvalidRestaurantException.class,
            () -> Restaurant.create("  ", "Rua A", "Italiana", "Seg-Sex 11h-23h", UUID.randomUUID()));
    }

    @Test
    @DisplayName("Proíbe endereço em branco")
    void rejectsBlankAddress() {
        assertThrows(InvalidRestaurantException.class,
            () -> Restaurant.create("Foo", "", "Italiana", "Seg-Sex 11h-23h", UUID.randomUUID()));
    }

    @Test
    @DisplayName("Proíbe tipo de cozinha em branco")
    void rejectsBlankCuisineType() {
        assertThrows(InvalidRestaurantException.class,
            () -> Restaurant.create("Foo", "Rua A", null, "Seg-Sex 11h-23h", UUID.randomUUID()));
    }

    @Test
    @DisplayName("Proíbe horário em branco")
    void rejectsBlankOpeningHours() {
        assertThrows(InvalidRestaurantException.class,
            () -> Restaurant.create("Foo", "Rua A", "Italiana", "  ", UUID.randomUUID()));
    }

    @Test
    @DisplayName("Proíbe ownerId nulo")
    void rejectsNullOwnerId() {
        assertThrows(InvalidRestaurantException.class,
            () -> Restaurant.create("Foo", "Rua A", "Italiana", "Seg-Sex 11h-23h", null));
    }

    @Test
    @DisplayName("Restore preserva id e ownerId sem validar")
    void restoresWithoutRegeneratingId() {
        var id = UUID.randomUUID();
        var ownerId = UUID.randomUUID();
        Restaurant r = Restaurant.restore(id, "Foo", "Rua A", "Italiana", "Seg-Sex 11h-23h", ownerId);

        assertEquals(id, r.getId());
        assertEquals(ownerId, r.getOwnerId());
    }

    @Test
    @DisplayName("Restore rejeita id nulo")
    void restoreRejectsNullId() {
        assertThrows(NullPointerException.class,
            () -> Restaurant.restore(null, "Foo", "Rua A", "Italiana", "Seg-Sex 11h-23h", UUID.randomUUID()));
    }
}
