package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.RestaurantViewModel;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.UserWithDetailsViewModel;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsResponseModel;
import com.fiap.restaurant_management_v2.application.usecases.user.get_user_by_id.GetUserByIdOutputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.user.get_user_by_id.GetUserByIdResponseModel;

public class GetUserByIdPresenter implements GetUserByIdOutputBoundary {

    private UserWithDetailsViewModel viewModel;

    @Override
    public void present(GetUserByIdResponseModel response) {
        this.viewModel = new UserWithDetailsViewModel(
            response.id().toString(),
            response.name(),
            response.email(),
            response.login(),
            CpfFormatter.format(response.taxIdentifier()),
            response.userTypeName(),
            response.restaurants().stream().map(this::toRestaurantViewModel).toList()
        );
    }

    private RestaurantViewModel toRestaurantViewModel(RestaurantDsResponseModel r) {
        return new RestaurantViewModel(
            r.id().toString(), r.name(), r.address(),
            CnpjFormatter.format(r.taxIdentifier()), r.cuisineType(), r.openingHours(),
            r.ownerId().toString()
        );
    }

    public UserWithDetailsViewModel getViewModel() {
        return viewModel;
    }
}
