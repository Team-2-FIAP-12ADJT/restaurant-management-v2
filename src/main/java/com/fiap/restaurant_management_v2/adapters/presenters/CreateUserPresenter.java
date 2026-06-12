package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.UserViewModel;
import com.fiap.restaurant_management_v2.application.usecases.user.create.CreateUserOutputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.user.create.CreateUserResponseModel;

/**
 * Stateful per request: the interactor pushes the response model in, the delivery
 * mechanism reads the view model out. Must be request-scoped when wired — a
 * singleton would leak state across concurrent requests.
 */
public class CreateUserPresenter implements CreateUserOutputBoundary {

    private UserViewModel viewModel;

    @Override
    public void present(CreateUserResponseModel response) {
        this.viewModel = new UserViewModel(
            response.id().toString(),
            response.name(),
            response.email(),
            response.login()
        );
    }

    public UserViewModel getViewModel() {
        return viewModel;
    }
}
