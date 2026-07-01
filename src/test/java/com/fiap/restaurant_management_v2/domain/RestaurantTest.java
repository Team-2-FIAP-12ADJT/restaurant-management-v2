package com.fiap.restaurant_management_v2.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fiap.restaurant_management_v2.domain.exception.InvalidRestaurantException;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RestaurantTest {

    private static final String VALID_CNPJ = "12345678000199";

    @Test
    @DisplayName("Cria restaurante com sucesso e gera id")
    void createsRestaurantSuccessfully() {
        var ownerId = UUID.randomUUID();
        Restaurant r = Restaurant.create(
            "Foo",
            "Rua A",
            VALID_CNPJ,
            "Italiana",
            "Seg-Sex 11h-23h",
            ownerId
        );

        assertNotNull(r.getId());
        assertEquals("Foo", r.getName());
        assertEquals("Rua A", r.getAddress());
        assertEquals(VALID_CNPJ, r.getTaxIdentifier());
        assertEquals("Italiana", r.getCuisineType());
        assertEquals("Seg-Sex 11h-23h", r.getOpeningHours());
        assertEquals(ownerId, r.getOwnerId());
    }

    @Test
    @DisplayName("Proíbe nome em branco")
    void rejectsBlankName() {
        assertThrows(InvalidRestaurantException.class, () ->
            Restaurant.create(
                "  ",
                "Rua A",
                VALID_CNPJ,
                "Italiana",
                "Seg-Sex 11h-23h",
                UUID.randomUUID()
            )
        );
    }

    @Test
    @DisplayName("Proíbe endereço em branco")
    void rejectsBlankAddress() {
        assertThrows(InvalidRestaurantException.class, () ->
            Restaurant.create(
                "Foo",
                "",
                VALID_CNPJ,
                "Italiana",
                "Seg-Sex 11h-23h",
                UUID.randomUUID()
            )
        );
    }

    @Test
    @DisplayName("Proíbe tipo de cozinha em branco")
    void rejectsBlankCuisineType() {
        assertThrows(InvalidRestaurantException.class, () ->
            Restaurant.create(
                "Foo",
                "Rua A",
                VALID_CNPJ,
                null,
                "Seg-Sex 11h-23h",
                UUID.randomUUID()
            )
        );
    }

    @Test
    @DisplayName("Proíbe horário em branco")
    void rejectsBlankOpeningHours() {
        assertThrows(InvalidRestaurantException.class, () ->
            Restaurant.create(
                "Foo",
                "Rua A",
                VALID_CNPJ,
                "Italiana",
                "  ",
                UUID.randomUUID()
            )
        );
    }

    @ParameterizedTest
    @DisplayName("Proíbe CNPJ inválido")
    @ValueSource(strings = { "123", "1234567890019A", "12345678000190019" })
    void rejectsInvalidCnpj(String cnpj) {
        assertThrows(InvalidRestaurantException.class, () ->
            Restaurant.create(
                "Foo",
                "Rua A",
                cnpj,
                "Italiana",
                "Seg-Sex 11h-23h",
                UUID.randomUUID()
            )
        );
    }

    @Test
    @DisplayName("Proíbe ownerId nulo")
    void rejectsNullOwnerId() {
        assertThrows(InvalidRestaurantException.class, () ->
            Restaurant.create(
                "Foo",
                "Rua A",
                VALID_CNPJ,
                "Italiana",
                "Seg-Sex 11h-23h",
                null
            )
        );
    }

    @Test
    @DisplayName("Restore preserva id e ownerId sem validar")
    void restoresWithoutRegeneratingId() {
        var id = UUID.randomUUID();
        var ownerId = UUID.randomUUID();
        Restaurant r = Restaurant.restore(
            id,
            "Foo",
            "Rua A",
            VALID_CNPJ,
            "Italiana",
            "Seg-Sex 11h-23h",
            ownerId
        );

        assertEquals(id, r.getId());
        assertEquals(ownerId, r.getOwnerId());
    }

    @Test
    @DisplayName("Restore rejeita id nulo")
    void restoreRejectsNullId() {
        assertThrows(NullPointerException.class, () ->
            Restaurant.restore(
                null,
                "Foo",
                "Rua A",
                VALID_CNPJ,
                "Italiana",
                "Seg-Sex 11h-23h",
                UUID.randomUUID()
            )
        );
    }
}
