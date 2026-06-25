package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.PageViewModel;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.UserTypeViewModel;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import com.fiap.restaurant_management_v2.application.usecases.usertype.get_all.GetAllUsersTypeOutputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.usertype.get_all.GetAllUsersTypeResponseModel;
import com.fiap.restaurant_management_v2.application.usecases.usertype.get_all.UserTypeSummary;


//alterar
public class GetAllUsersTypePresenter implements GetAllUsersTypeOutputBoundary {

    private PageViewModel<UserTypeViewModel> viewModel;

    @Override
    public void present(GetAllUsersTypeResponseModel response) {
        PageResult<UserTypeSummary> page = response.page();

        this.viewModel = new PageViewModel<>(
            page.page(),
            page.size(),
            page.totalElements(),
            page.totalPages(),
            page.content().stream().map(this::toViewModel).toList()
        );
    }

    public PageViewModel<UserTypeViewModel> getViewModel() {
        return viewModel;
    }

    private UserTypeViewModel toViewModel(UserTypeSummary summary) {
        return new UserTypeViewModel(
            summary.id().toString(),
            summary.userType()
        );
    }
}
