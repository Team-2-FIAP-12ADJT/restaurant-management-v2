package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.UserTypeViewModel;
import com.fiap.restaurant_management_v2.application.usecases.usertype.get_type_by_id.GetUserTypeByIdOutputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.usertype.get_type_by_id.GetUserTypeByIdResponseModel;

public class GetUserTypeByIdPresenter implements GetUserTypeByIdOutputBoundary {

    //acertar
    private UserTypeViewModel viewModel;

    @Override
    public void present(GetUserTypeByIdResponseModel response) {
        this.viewModel = new UserTypeViewModel(
            response.id().toString(),
            response.userType()
        );
    }

    public UserTypeViewModel getViewModel() {
        return viewModel;
    }


}
