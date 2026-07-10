package com.fiap.restaurant_management_v2.domain;

import com.fiap.restaurant_management_v2.domain.exception.InvalidMenuItemException;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public final class MenuItem {

    public static final int MAX_NAME_LENGTH = 255;
    public static final int MAX_DESCRIPTION_LENGTH = 1000;
    public static final int MAX_PHOTO_PATH_LENGTH = 500;

    private final UUID id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final boolean onlyLocal;
    private final String photoPath;
    private final UUID restaurantId;

    private MenuItem(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        boolean onlyLocal,
        String photoPath,
        UUID restaurantId
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.onlyLocal = onlyLocal;
        this.photoPath = photoPath;
        this.restaurantId = restaurantId;
    }

    public static MenuItem create(
        String name,
        String description,
        BigDecimal price,
        boolean onlyLocal,
        String photoPath,
        UUID restaurantId
    ) {
        MenuItem menuItem = new MenuItem(
            UUID.randomUUID(),
            name,
            description,
            price,
            onlyLocal,
            photoPath,
            restaurantId
        );
        menuItem.validate();
        return menuItem;
    }

    public static MenuItem update(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        boolean onlyLocal,
        String photoPath,
        UUID restaurantId
    ) {
        MenuItem menuItem = new MenuItem(
            Objects.requireNonNull(id, "id"),
            name,
            description,
            price,
            onlyLocal,
            photoPath,
            restaurantId
        );
        menuItem.validate();
        return menuItem;
    }

    public static MenuItem restore(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        boolean onlyLocal,
        String photoPath,
        UUID restaurantId
    ) {
        return new MenuItem(
            Objects.requireNonNull(id, "id"),
            name,
            description,
            price,
            onlyLocal,
            photoPath,
            Objects.requireNonNull(restaurantId, "restaurantId")
        );
    }

    private void validate() {
        validateDetails();
    }

    private void validateDetails() {
        if (isBlank(name)) {
            throw new InvalidMenuItemException("Nome do item do cardápio é obrigatório");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new InvalidMenuItemException(
                "Nome do item do cardápio deve ter no máximo "
                    + MAX_NAME_LENGTH
                    + " caracteres"
            );
        }
        if (isBlank(description)) {
            throw new InvalidMenuItemException("Descrição do item do cardápio é obrigatória");
        }
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new InvalidMenuItemException(
                "Descrição do item do cardápio deve ter no máximo "
                    + MAX_DESCRIPTION_LENGTH
                    + " caracteres"
            );
        }
        if (price == null || price.signum() <= 0) {
            throw new InvalidMenuItemException("Preço do item do cardápio deve ser maior que zero");
        }
        if (isBlank(photoPath)) {
            throw new InvalidMenuItemException("Caminho da foto do item do cardápio é obrigatório");
        }
        if (photoPath.length() > MAX_PHOTO_PATH_LENGTH) {
            throw new InvalidMenuItemException(
                "Caminho da foto do item do cardápio deve ter no máximo "
                    + MAX_PHOTO_PATH_LENGTH
                    + " caracteres"
            );
        }
        if (restaurantId == null) {
            throw new InvalidMenuItemException("Restaurante do item do cardápio é obrigatório");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean isOnlyLocal() {
        return onlyLocal;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public UUID getRestaurantId() {
        return restaurantId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MenuItem other)) {
            return false;
        }
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
