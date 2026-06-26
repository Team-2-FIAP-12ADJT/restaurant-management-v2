package com.fiap.restaurant_management_v2.domain;

import com.fiap.restaurant_management_v2.domain.exception.InvalidRestaurantException;
import java.util.Objects;
import java.util.UUID;

public final class Restaurant {

    private final UUID id;
    private final String name;
    private final String address;
    private final String cuisineType;
    private final String openingHours;
    private final UUID ownerId;

    private Restaurant(
        UUID id,
        String name,
        String address,
        String cuisineType,
        String openingHours,
        UUID ownerId
    ) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.cuisineType = cuisineType;
        this.openingHours = openingHours;
        this.ownerId = ownerId;
    }

    public static Restaurant create(
        String name,
        String address,
        String cuisineType,
        String openingHours,
        UUID ownerId
    ) {
        Restaurant restaurant = new Restaurant(
            UUID.randomUUID(),
            name,
            address,
            cuisineType,
            openingHours,
            ownerId
        );
        restaurant.validate();
        return restaurant;
    }

    public static Restaurant restore(
        UUID id,
        String name,
        String address,
        String cuisineType,
        String openingHours,
        UUID ownerId
    ) {
        return new Restaurant(
            Objects.requireNonNull(id, "id"),
            name,
            address,
            cuisineType,
            openingHours,
            Objects.requireNonNull(ownerId, "ownerId")
        );
    }

    private void validate() {
        if (isBlank(name)) {
            throw new InvalidRestaurantException("Nome do restaurante é obrigatório");
        }
        if (isBlank(address)) {
            throw new InvalidRestaurantException("Endereço é obrigatório");
        }
        if (isBlank(cuisineType)) {
            throw new InvalidRestaurantException("Tipo de cozinha é obrigatório");
        }
        if (isBlank(openingHours)) {
            throw new InvalidRestaurantException("Horário de funcionamento é obrigatório");
        }
        if (ownerId == null) {
            throw new InvalidRestaurantException("Dono do restaurante é obrigatório");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getCuisineType() { return cuisineType; }
    public String getOpeningHours() { return openingHours; }
    public UUID getOwnerId() { return ownerId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Restaurant other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
