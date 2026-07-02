package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.PageViewModel;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.UserViewModel;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import com.fiap.restaurant_management_v2.application.usecases.user.get_all.GetAllUsersOutputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.user.get_all.GetAllUsersResponseModel;
import com.fiap.restaurant_management_v2.application.usecases.user.get_all.UserSummary;

public class GetAllUsersPresenter implements GetAllUsersOutputBoundary {

    private PageViewModel<UserViewModel> viewModel;

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

    public PageViewModel<UserViewModel> getViewModel() {
        return viewModel;
    }

    private UserViewModel toViewModel(UserSummary summary) {
        return new UserViewModel(
            summary.id().toString(),
            summary.name(),
            summary.email(),
            summary.login(),
            CpfFormatter.format(summary.taxIdentifier())
        );
    }
}
