package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.RestaurantWithOwnerViewModel;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.UserViewModel;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.get_by_id.GetRestaurantByIdOutputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.get_by_id.GetRestaurantByIdResponseModel;

public class GetRestaurantByIdPresenter
    implements GetRestaurantByIdOutputBoundary
{

    private RestaurantWithOwnerViewModel viewModel;

    @Override
    public void present(GetRestaurantByIdResponseModel response) {
        this.viewModel = new RestaurantWithOwnerViewModel(
            response.id().toString(),
            response.name(),
            response.address(),
            CnpjFormatter.format(response.taxIdentifier()),
            response.cuisineType(),
            response.openingHours(),
            toOwnerViewModel(response.owner())
        );
    }

    public RestaurantWithOwnerViewModel getViewModel() {
        return viewModel;
    }

    private UserViewModel toOwnerViewModel(UserDsResponseModel owner) {
        return new UserViewModel(
            owner.id().toString(),
            owner.name(),
            owner.email(),
            owner.login(),
            CpfFormatter.format(owner.taxIdentifier())
        );
    }
}
