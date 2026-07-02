package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.UserViewModel;
import com.fiap.restaurant_management_v2.application.usecases.user.update.UpdateUserOutputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.user.update.UpdateUserResponseModel;

public class UpdateUserPresenter implements UpdateUserOutputBoundary {

    private UserViewModel viewModel;

    @Override
    public void present(UpdateUserResponseModel response) {
        this.viewModel = new UserViewModel(
            response.id().toString(),
            response.name(),
            response.email(),
            response.login(),
            CpfFormatter.format(response.taxIdentifier())
        );
    }

    public UserViewModel getViewModel() {
        return viewModel;
    }
}
