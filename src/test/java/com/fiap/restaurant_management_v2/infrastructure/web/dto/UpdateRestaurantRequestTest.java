package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UpdateRestaurantRequest — Normalization in Constructor")
class UpdateRestaurantRequestTest {

    private static final UUID OWNER_ID = UUID.randomUUID();

    private static UpdateRestaurantRequest build(
        String name,
        String address,
        String taxIdentifier,
        String cuisineType,
        String openingHours
    ) {
        return new UpdateRestaurantRequest(
            name,
            address,
            taxIdentifier,
            cuisineType,
            openingHours,
            OWNER_ID
        );
    }

    // --- CAMPO NOME ---
    @Test
    @DisplayName("name: null stays null (PATCH — omitted field)")
    void nameNullStaysNull() {
        assertNull(build(null, "Av. Paulista", "12345678901234", "Italian", "10:00-22:00").name());
    }

    @Test
    @DisplayName("name: trims whitespace")
    void nameTrimmed() {
        assertEquals(
            "Trattoria",
            build("  Trattoria  ", "Av. Paulista", "12345678901234", "Italian", "10:00-22:00").name()
        );
    }

    // --- CAMPO ENDEREÇO ---
    @Test
    @DisplayName("address: null stays null (PATCH — omitted field)")
    void addressNullStaysNull() {
        assertNull(build("Trattoria", null, "12345678901234", "Italian", "10:00-22:00").address());
    }

    @Test
    @DisplayName("address: trims whitespace")
    void addressTrimmed() {
        assertEquals(
            "Av. Paulista 1000",
            build("Trattoria", "  Av. Paulista 1000  ", "12345678901234", "Italian", "10:00-22:00").address()
        );
    }

    // --- CAMPO IDENTIFICADOR FISCAL (CNPJ) ---
    @Test
    @DisplayName("taxIdentifier: null stays null (PATCH — omitted field)")
    void taxIdentifierNullStaysNull() {
        assertNull(build("Trattoria", "Av. Paulista", null, "Italian", "10:00-22:00").taxIdentifier());
    }

    @Test
    @DisplayName("taxIdentifier: removes formatting (dots, slashes, dashes) and converts to uppercase")
    void taxIdentifierNormalized() {
        // CNPJ formatado: XX.XXX.XXX/XXXX-XX -> XXXXXXXXXXXXXXXX
        assertEquals(
            "12345678901234",
            build("Trattoria", "Av. Paulista", "12.345.678/9012-34", "Italian", "10:00-22:00").taxIdentifier()
        );
    }

    @Test
    @DisplayName("taxIdentifier: converts lowercase to uppercase after formatting removal")
    void taxIdentifierUppercase() {
        assertEquals(
            "AB12CD34EFGH5678",
            build("Trattoria", "Av. Paulista", "ab.12cd.34ef/gh56-78", "Italian", "10:00-22:00").taxIdentifier()
        );
    }

    // --- CAMPO TIPO DE COZINHA ---
    @Test
    @DisplayName("cuisineType: null stays null (PATCH — omitted field)")
    void cuisineTypeNullStaysNull() {
        assertNull(build("Trattoria", "Av. Paulista", "12345678901234", null, "10:00-22:00").cuisineType());
    }

    @Test
    @DisplayName("cuisineType: trims whitespace")
    void cuisineTypeTrimmed() {
        assertEquals(
            "Italian",
            build("Trattoria", "Av. Paulista", "12345678901234", "  Italian  ", "10:00-22:00").cuisineType()
        );
    }

    // --- CAMPO HORÁRIO DE FUNCIONAMENTO ---
    @Test
    @DisplayName("openingHours: null stays null (PATCH — omitted field)")
    void openingHoursNullStaysNull() {
        assertNull(build("Trattoria", "Av. Paulista", "12345678901234", "Italian", null).openingHours());
    }

    @Test
    @DisplayName("openingHours: trims whitespace")
    void openingHoursTrimmed() {
        assertEquals(
            "10:00-22:00",
            build("Trattoria", "Av. Paulista", "12345678901234", "Italian", "  10:00-22:00  ").openingHours()
        );
    }

    // --- TESTES DE COMBINAÇÃO ---
    @Test
    @DisplayName("Constructor: handles all fields null together (full PATCH omission)")
    void allFieldsNullTogether() {
        var req = build(null, null, null, null, null);
        assertNull(req.name());
        assertNull(req.address());
        assertNull(req.taxIdentifier());
        assertNull(req.cuisineType());
        assertNull(req.openingHours());
    }

    @Test
    @DisplayName("Constructor: normalizes all fields simultaneously")
    void allFieldsNormalizedSimultaneously() {
        var req = build(
            "  Trattoria  ",
            "  Av. Paulista  ",
            "12.345.678/9012-34",
            "  Italian  ",
            "  10:00-22:00  "
        );
        assertEquals("Trattoria", req.name());
        assertEquals("Av. Paulista", req.address());
        assertEquals("12345678901234", req.taxIdentifier());
        assertEquals("Italian", req.cuisineType());
        assertEquals("10:00-22:00", req.openingHours());
    }
}

