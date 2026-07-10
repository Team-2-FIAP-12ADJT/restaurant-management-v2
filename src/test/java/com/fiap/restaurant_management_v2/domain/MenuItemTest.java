package com.fiap.restaurant_management_v2.domain;

import com.fiap.restaurant_management_v2.domain.exception.InvalidMenuItemException;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MenuItemTest {

    private static final BigDecimal VALID_PRICE = new BigDecimal("39.90");
    private static final UUID RESTAURANT_ID = UUID.randomUUID();

    @Test
    @DisplayName("Cria item do cardápio com sucesso e gera id")
    void createsMenuItemSuccessfully() {
        MenuItem menuItem = MenuItem.create(
            "Risoto",
            "Risoto de cogumelos",
            VALID_PRICE,
            true,
            "/images/risoto.jpg",
            RESTAURANT_ID
        );

        assertNotNull(menuItem.getId());
        assertEquals("Risoto", menuItem.getName());
        assertEquals("Risoto de cogumelos", menuItem.getDescription());
        assertEquals(VALID_PRICE, menuItem.getPrice());
        assertTrue(menuItem.isOnlyLocal());
        assertEquals("/images/risoto.jpg", menuItem.getPhotoPath());
        assertEquals(RESTAURANT_ID, menuItem.getRestaurantId());
    }

    @Test
    @DisplayName("Permite item que não é exclusivo para consumo local")
    void allowsMenuItemThatIsNotOnlyLocal() {
        MenuItem menuItem = createValidMenuItem(false);

        assertFalse(menuItem.isOnlyLocal());
    }

    @Test
    @DisplayName("Proíbe nome em branco")
    void rejectsBlankName() {
        assertThrows(
            InvalidMenuItemException.class,
            () -> MenuItem.create(
                "  ",
                "Descricao",
                VALID_PRICE,
                false,
                "/images/item.jpg",
                RESTAURANT_ID
            )
        );
    }

    @Test
    @DisplayName("Proíbe descrição em branco")
    void rejectsBlankDescription() {
        assertThrows(
            InvalidMenuItemException.class,
            () -> MenuItem.create(
                "Item",
                null,
                VALID_PRICE,
                false,
                "/images/item.jpg",
                RESTAURANT_ID
            )
        );
    }

    @Test
    @DisplayName("Proíbe preço nulo, zero ou negativo")
    void rejectsInvalidPrice() {
        assertThrows(
            InvalidMenuItemException.class,
            () -> createMenuItemWithPrice(null)
        );
        assertThrows(
            InvalidMenuItemException.class,
            () -> createMenuItemWithPrice(BigDecimal.ZERO)
        );
        assertThrows(
            InvalidMenuItemException.class,
            () -> createMenuItemWithPrice(new BigDecimal("-0.01"))
        );
    }

    @Test
    @DisplayName("Proíbe caminho da foto em branco")
    void rejectsBlankPhotoPath() {
        assertThrows(
            InvalidMenuItemException.class,
            () -> MenuItem.create(
                "Item",
                "Descricao",
                VALID_PRICE,
                false,
                "",
                RESTAURANT_ID
            )
        );
    }

    @Test
    @DisplayName("Proíbe campos de texto maiores que os limites")
    void rejectsTextFieldsThatExceedLimits() {
        assertThrows(
            InvalidMenuItemException.class,
            () -> MenuItem.create(
                "N".repeat(MenuItem.MAX_NAME_LENGTH + 1),
                "Descricao",
                VALID_PRICE,
                false,
                "/images/item.jpg",
                RESTAURANT_ID
            )
        );
        assertThrows(
            InvalidMenuItemException.class,
            () -> MenuItem.create(
                "Item",
                "D".repeat(MenuItem.MAX_DESCRIPTION_LENGTH + 1),
                VALID_PRICE,
                false,
                "/images/item.jpg",
                RESTAURANT_ID
            )
        );
        assertThrows(
            InvalidMenuItemException.class,
            () -> MenuItem.create(
                "Item",
                "Descricao",
                VALID_PRICE,
                false,
                "P".repeat(MenuItem.MAX_PHOTO_PATH_LENGTH + 1),
                RESTAURANT_ID
            )
        );
    }

    @Test
    @DisplayName("Proíbe restaurantId nulo")
    void rejectsNullRestaurantId() {
        assertThrows(
            InvalidMenuItemException.class,
            () -> MenuItem.create(
                "Item",
                "Descricao",
                VALID_PRICE,
                false,
                "/images/item.jpg",
                null
            )
        );
    }

    @Test
    @DisplayName("Update valida e preserva o id fornecido")
    void updatesWithProvidedIdAndValidates() {
        UUID id = UUID.randomUUID();

        MenuItem menuItem = MenuItem.update(
            id,
            "Risoto especial",
            "Risoto de cogumelos frescos",
            new BigDecimal("49.90"),
            false,
            "/images/risoto-especial.jpg",
            RESTAURANT_ID
        );

        assertEquals(id, menuItem.getId());
        assertEquals("Risoto especial", menuItem.getName());
        assertEquals(RESTAURANT_ID, menuItem.getRestaurantId());
    }

    @Test
    @DisplayName("Update rejeita dados inválidos")
    void updateRejectsInvalidData() {
        UUID id = UUID.randomUUID();

        assertThrows(
            InvalidMenuItemException.class,
            () -> MenuItem.update(
                id,
                "  ",
                "Descricao",
                VALID_PRICE,
                false,
                "/images/item.jpg",
                RESTAURANT_ID
            )
        );
    }

    @Test
    @DisplayName("Update rejeita id nulo")
    void updateRejectsNullId() {
        assertThrows(
            NullPointerException.class,
            () -> MenuItem.update(
                null,
                "Item",
                "Descricao",
                VALID_PRICE,
                false,
                "/images/item.jpg",
                RESTAURANT_ID
            )
        );
    }

    @Test
    @DisplayName("Restore preserva id e restaurantId")
    void restoresWithoutRegeneratingId() {
        UUID id = UUID.randomUUID();

        MenuItem menuItem = MenuItem.restore(
            id,
            "Item",
            "Descricao",
            VALID_PRICE,
            false,
            "/images/item.jpg",
            RESTAURANT_ID
        );

        assertEquals(id, menuItem.getId());
        assertEquals(RESTAURANT_ID, menuItem.getRestaurantId());
    }

    @Test
    @DisplayName("Restore rejeita identificadores nulos")
    void restoreRejectsNullIdentifiers() {
        assertThrows(
            NullPointerException.class,
            () -> MenuItem.restore(
                null,
                "Item",
                "Descricao",
                VALID_PRICE,
                false,
                "/images/item.jpg",
                RESTAURANT_ID
            )
        );
        assertThrows(
            NullPointerException.class,
            () -> MenuItem.restore(
                UUID.randomUUID(),
                "Item",
                "Descricao",
                VALID_PRICE,
                false,
                "/images/item.jpg",
                null
            )
        );
    }

    @Test
    @DisplayName("Itens com o mesmo id são iguais")
    void considersMenuItemsWithSameIdEqual() {
        UUID id = UUID.randomUUID();
        MenuItem first = MenuItem.restore(
            id,
            "Item A",
            "Descricao A",
            VALID_PRICE,
            false,
            "/images/a.jpg",
            RESTAURANT_ID
        );
        MenuItem second = MenuItem.restore(
            id,
            "Item B",
            "Descricao B",
            new BigDecimal("59.90"),
            true,
            "/images/b.jpg",
            UUID.randomUUID()
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    @DisplayName("Itens com ids diferentes não são iguais")
    void considersMenuItemsWithDifferentIdsNotEqual() {
        MenuItem first = MenuItem.restore(
            UUID.randomUUID(),
            "Item A",
            "Descricao A",
            VALID_PRICE,
            false,
            "/images/a.jpg",
            RESTAURANT_ID
        );
        MenuItem second = MenuItem.restore(
            UUID.randomUUID(),
            "Item B",
            "Descricao B",
            VALID_PRICE,
            false,
            "/images/b.jpg",
            RESTAURANT_ID
        );

        assertNotEquals(first, second);
    }

    @Test
    @DisplayName("Igualdade trata a mesma referência e outros tipos")
    void handlesSameReferenceAndOtherTypes() {
        MenuItem menuItem = createValidMenuItem(false);
        Object sameReference = menuItem;
        Object differentType = new Object();

        assertEquals(menuItem, sameReference);
        assertNotEquals(menuItem, differentType);
    }

    private static MenuItem createValidMenuItem(boolean onlyLocal) {
        return MenuItem.create(
            "Item",
            "Descricao",
            VALID_PRICE,
            onlyLocal,
            "/images/item.jpg",
            RESTAURANT_ID
        );
    }

    private static MenuItem createMenuItemWithPrice(BigDecimal price) {
        return MenuItem.create(
            "Item",
            "Descricao",
            price,
            false,
            "/images/item.jpg",
            RESTAURANT_ID
        );
    }
}
