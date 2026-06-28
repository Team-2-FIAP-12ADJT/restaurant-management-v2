package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.UserTypeViewModel;
import com.fiap.restaurant_management_v2.application.usecases.usertype.create.CreateUserTypeOutputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.usertype.create.CreateUserTypeResponseModel;

/**
 * Stateful per request: the interactor pushes the response model in, the delivery
 * mechanism reads the view model out. Must be request-scoped when wired — a
 * singleton would leak state across concurrent requests.
 */
public class CreateUserTypePresenter implements CreateUserTypeOutputBoundary {

    private UserTypeViewModel viewModel ;

    @Override
    public void present(CreateUserTypeResponseModel response) {
        this.viewModel = new UserTypeViewModel(
                response.id().toString() ,
                response.userType()
        );
    }


    public UserTypeViewModel  getViewModel() {
        return viewModel;
    }


}
