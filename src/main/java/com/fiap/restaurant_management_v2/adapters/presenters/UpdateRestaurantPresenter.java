package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.RestaurantViewModel;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.update.UpdateRestaurantOutputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.update.UpdateRestaurantResponseModel;

public class UpdateRestaurantPresenter implements UpdateRestaurantOutputBoundary {

    private RestaurantViewModel viewModel;

    @Override
    public void present(UpdateRestaurantResponseModel response) {
        this.viewModel = new RestaurantViewModel(
            response.id().toString(),
            response.name(),
            response.address(),
            response.cuisineType(),
            response.openingHours(),
            response.ownerId().toString()
        );
    }

    public RestaurantViewModel getViewModel() {
        return viewModel;
    }
}
