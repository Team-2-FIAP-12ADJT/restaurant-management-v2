package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CreateRestaurantRequest — Normalization in Constructor")
class CreateRestaurantRequestTest {

    private static final UUID OWNER_ID = UUID.randomUUID();

    private static CreateRestaurantRequest build(
        String name,
        String address,
        String taxIdentifier,
        String cuisineType,
        String openingHours,
        UUID ownerId
    ) {
        return new CreateRestaurantRequest(
            name,
            address,
            taxIdentifier,
            cuisineType,
            openingHours,
            ownerId
        );
    }

    @Test
    @DisplayName("All fields normalized and trimmed together")
    void normalizesTaxIdentifierAndTrims() {
        var req = new CreateRestaurantRequest(
            " Foo ",
            " Rua 1 ",
            "12.345.678/0001-99",
            " Italiana ",
            " 09-18 ",
            OWNER_ID
        );

        assertEquals("Foo", req.name());
        assertEquals("Rua 1", req.address());
        assertEquals("12345678000199", req.taxIdentifier());
        assertEquals("Italiana", req.cuisineType());
        assertEquals("09-18", req.openingHours());
        assertEquals(OWNER_ID, req.ownerId());
    }

    @Test
    @DisplayName("All null values remain null")
    void nullValuesRemainNull() {
        var req = new CreateRestaurantRequest(null, null, null, null, null, null);
        assertNull(req.name());
        assertNull(req.address());
        assertNull(req.taxIdentifier());
        assertNull(req.cuisineType());
        assertNull(req.openingHours());
        assertNull(req.ownerId());
    }

    // --- CAMPO NOME ---
    @Test
    @DisplayName("name: null stays null")
    void nameNullStaysNull() {
        var req = build(null, "Rua 1", "12345678000199", "Italiana", "09-18", OWNER_ID);
        assertNull(req.name());
    }

    @Test
    @DisplayName("name: trims whitespace")
    void nameTrimmed() {
        var req = build("  Foo  ", "Rua 1", "12345678000199", "Italiana", "09-18", OWNER_ID);
        assertEquals("Foo", req.name());
    }

    // --- CAMPO ENDEREÇO ---
    @Test
    @DisplayName("address: null stays null")
    void addressNullStaysNull() {
        var req = build("Foo", null, "12345678000199", "Italiana", "09-18", OWNER_ID);
        assertNull(req.address());
    }

    @Test
    @DisplayName("address: trims whitespace")
    void addressTrimmed() {
        var req = build("Foo", "  Rua 1  ", "12345678000199", "Italiana", "09-18", OWNER_ID);
        assertEquals("Rua 1", req.address());
    }

    // --- CAMPO IDENTIFICADOR FISCAL (CNPJ) ---
    @Test
    @DisplayName("taxIdentifier: null stays null")
    void taxIdentifierNullStaysNull() {
        var req = build("Foo", "Rua 1", null, "Italiana", "09-18", OWNER_ID);
        assertNull(req.taxIdentifier());
    }

    @Test
    @DisplayName("taxIdentifier: removes formatting and converts to uppercase")
    void taxIdentifierNormalized() {
        var req = build("Foo", "Rua 1", "12.345.678/0001-99", "Italiana", "09-18", OWNER_ID);
        assertEquals("12345678000199", req.taxIdentifier());
    }

    @Test
    @DisplayName("taxIdentifier: converts lowercase to uppercase after formatting removal")
    void taxIdentifierUppercase() {
        var req = build("Foo", "Rua 1", "ab.cd.ef/gh01-23", "Italiana", "09-18", OWNER_ID);
        assertEquals("ABCDEFGH0123", req.taxIdentifier());
    }

    // --- CAMPO TIPO DE COZINHA ---
    @Test
    @DisplayName("cuisineType: null stays null")
    void cuisineTypeNullStaysNull() {
        var req = build("Foo", "Rua 1", "12345678000199", null, "09-18", OWNER_ID);
        assertNull(req.cuisineType());
    }

    @Test
    @DisplayName("cuisineType: trims whitespace")
    void cuisineTypeTrimmed() {
        var req = build("Foo", "Rua 1", "12345678000199", "  Italiana  ", "09-18", OWNER_ID);
        assertEquals("Italiana", req.cuisineType());
    }

    // --- CAMPO HORÁRIO DE FUNCIONAMENTO ---
    @Test
    @DisplayName("openingHours: null stays null")
    void openingHoursNullStaysNull() {
        var req = build("Foo", "Rua 1", "12345678000199", "Italiana", null, OWNER_ID);
        assertNull(req.openingHours());
    }

    @Test
    @DisplayName("openingHours: trims whitespace")
    void openingHoursTrimmed() {
        var req = build("Foo", "Rua 1", "12345678000199", "Italiana", "  09-18  ", OWNER_ID);
        assertEquals("09-18", req.openingHours());
    }

    // --- CAMPO ID DO DONO ---
    @Test
    @DisplayName("ownerId: passed through unchanged")
    void ownerIdPassedThrough() {
        var req = build("Foo", "Rua 1", "12345678000199", "Italiana", "09-18", OWNER_ID);
        assertEquals(OWNER_ID, req.ownerId());
    }

    @Test
    @DisplayName("ownerId: null stays null")
    void ownerIdNullStaysNull() {
        var req = build("Foo", "Rua 1", "12345678000199", "Italiana", "09-18", null);
        assertNull(req.ownerId());
    }
}

