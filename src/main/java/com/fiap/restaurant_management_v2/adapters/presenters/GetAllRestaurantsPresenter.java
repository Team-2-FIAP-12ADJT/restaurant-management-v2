package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.PageViewModel;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.RestaurantViewModel;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.get_all.GetAllRestaurantsOutputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.get_all.GetAllRestaurantsResponseModel;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.get_all.RestaurantSummary;

public class GetAllRestaurantsPresenter implements GetAllRestaurantsOutputBoundary {

    private PageViewModel<RestaurantViewModel> viewModel;

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

    public PageViewModel<RestaurantViewModel> getViewModel() {
        return viewModel;
    }

    private RestaurantViewModel toViewModel(RestaurantSummary summary) {
        return new RestaurantViewModel(
            summary.id().toString(),
            summary.name(),
            summary.address(),
            summary.cuisineType(),
            summary.openingHours(),
            summary.ownerId().toString()
        );
    }
}
