package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.TokenViewModel;
import com.fiap.restaurant_management_v2.application.usecases.auth.login.LoginOutputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.auth.login.LoginResponseModel;

public class AuthPresenter implements LoginOutputBoundary {

    private TokenViewModel viewModel;

    @Override
    public void present(LoginResponseModel response) {
        this.viewModel = new TokenViewModel(response.accessToken(), response.expiresAt());
    }

    public TokenViewModel getViewModel() {
        return viewModel;
    }
}
