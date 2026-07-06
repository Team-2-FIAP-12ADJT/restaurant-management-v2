package com.fiap.restaurant_management_v2.adapters.presenters.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ViewModelRecordsTest {

    @Test
    void restaurantViewModelExposesAllComponents() {
        var viewModel = new RestaurantViewModel(
            "restaurant-id",
            "Cantina",
            "Rua A",
            "12.345.678/0001-90",
            "Italiana",
            "09:00-18:00",
            "owner-id"
        );

        assertEquals("restaurant-id", viewModel.id());
        assertEquals("Cantina", viewModel.name());
        assertEquals("Rua A", viewModel.address());
        assertEquals("12.345.678/0001-90", viewModel.taxIdentifier());
        assertEquals("Italiana", viewModel.cuisineType());
        assertEquals("09:00-18:00", viewModel.openingHours());
        assertEquals("owner-id", viewModel.ownerId());
    }
}
