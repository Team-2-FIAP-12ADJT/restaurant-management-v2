package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.MenuItemViewModel;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.PageViewModel;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_all.GetAllMenuItemsOutputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_all.GetAllMenuItemsResponseModel;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_all.MenuItemSummary;

public class GetAllMenuItemsPresenter
    implements GetAllMenuItemsOutputBoundary {

    private PageViewModel<MenuItemViewModel> viewModel;

    @Override
    public void present(GetAllMenuItemsResponseModel response) {
        this.viewModel = toViewModel(response.page());
    }

    public PageViewModel<MenuItemViewModel> getViewModel() {
        return viewModel;
    }

    static PageViewModel<MenuItemViewModel> toViewModel(
        PageResult<MenuItemSummary> page
    ) {
        return new PageViewModel<>(
            page.page(),
            page.size(),
            page.totalElements(),
            page.totalPages(),
            page.content().stream().map(GetAllMenuItemsPresenter::toItem).toList()
        );
    }

    private static MenuItemViewModel toItem(MenuItemSummary summary) {
        return new MenuItemViewModel(
            summary.id().toString(),
            summary.name(),
            summary.description(),
            summary.price(),
            summary.onlyLocal(),
            summary.photoPath(),
            summary.restaurantId().toString()
        );
    }
}
