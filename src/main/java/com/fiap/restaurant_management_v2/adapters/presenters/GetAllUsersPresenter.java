package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.PageViewModel;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.RestaurantViewModel;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.UserWithDetailsViewModel;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsResponseModel;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import com.fiap.restaurant_management_v2.application.usecases.user.get_all.GetAllUsersOutputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.user.get_all.GetAllUsersResponseModel;
import com.fiap.restaurant_management_v2.application.usecases.user.get_all.UserSummary;

public class GetAllUsersPresenter implements GetAllUsersOutputBoundary {

    private PageViewModel<UserWithDetailsViewModel> viewModel;

    @Override
    public void present(GetAllUsersResponseModel response) {
        PageResult<UserSummary> page = response.page();

        this.viewModel = new PageViewModel<>(
            page.page(),
            page.size(),
            page.totalElements(),
            page.totalPages(),
            page.content().stream().map(this::toViewModel).toList()
        );
    }

    public PageViewModel<UserWithDetailsViewModel> getViewModel() {
        return viewModel;
    }

    private UserWithDetailsViewModel toViewModel(UserSummary summary) {
        return new UserWithDetailsViewModel(
            summary.id().toString(),
            summary.name(),
            summary.email(),
            summary.login(),
            CpfFormatter.format(summary.taxIdentifier()),
            summary.userTypeName(),
            summary.restaurants().stream().map(this::toRestaurantViewModel).toList()
        );
    }

    private RestaurantViewModel toRestaurantViewModel(RestaurantDsResponseModel r) {
        return new RestaurantViewModel(
            r.id().toString(), r.name(), r.address(),
            CnpjFormatter.format(r.taxIdentifier()), r.cuisineType(), r.openingHours(),
            r.ownerId().toString()
        );
    }
}
