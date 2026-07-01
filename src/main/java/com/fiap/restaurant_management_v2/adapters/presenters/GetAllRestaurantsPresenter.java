package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.PageViewModel;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.RestaurantWithOwnerViewModel;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.UserViewModel;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.get_all.GetAllRestaurantsOutputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.get_all.GetAllRestaurantsResponseModel;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.get_all.RestaurantSummary;

public class GetAllRestaurantsPresenter
    implements GetAllRestaurantsOutputBoundary
{

    private PageViewModel<RestaurantWithOwnerViewModel> viewModel;

    @Override
    public void present(GetAllRestaurantsResponseModel response) {
        PageResult<RestaurantSummary> page = response.page();

        this.viewModel = new PageViewModel<>(
            page.page(),
            page.size(),
            page.totalElements(),
            page.totalPages(),
            page.content().stream().map(this::toViewModel).toList()
        );
    }

    public PageViewModel<RestaurantWithOwnerViewModel> getViewModel() {
        return viewModel;
    }

    private RestaurantWithOwnerViewModel toViewModel(RestaurantSummary summary) {
        return new RestaurantWithOwnerViewModel(
            summary.id().toString(),
            summary.name(),
            summary.address(),
            CnpjFormatter.format(summary.taxIdentifier()),
            summary.cuisineType(),
            summary.openingHours(),
            toOwnerViewModel(summary.owner())
        );
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
