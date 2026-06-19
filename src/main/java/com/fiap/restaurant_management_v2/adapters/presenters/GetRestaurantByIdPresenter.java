package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.RestaurantViewModel;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.get_by_id.GetRestaurantByIdOutputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.get_by_id.GetRestaurantByIdResponseModel;

public class GetRestaurantByIdPresenter implements GetRestaurantByIdOutputBoundary {

    private RestaurantViewModel viewModel;

    @Override
    public void present(GetRestaurantByIdResponseModel response) {
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
