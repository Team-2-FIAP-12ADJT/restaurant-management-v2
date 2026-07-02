package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.UserViewModel;
import com.fiap.restaurant_management_v2.application.usecases.user.get_user_by_id.GetUserByIdOutputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.user.get_user_by_id.GetUserByIdResponseModel;

public class GetUserByIdPresenter implements GetUserByIdOutputBoundary {

    private UserViewModel viewModel;

    @Override
    public void present(GetUserByIdResponseModel response) {
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
