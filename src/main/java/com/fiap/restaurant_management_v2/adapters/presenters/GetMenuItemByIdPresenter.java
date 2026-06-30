package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.MenuItemViewModel;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_by_id.GetMenuItemByIdOutputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_by_id.GetMenuItemByIdResponseModel;

public class GetMenuItemByIdPresenter
    implements GetMenuItemByIdOutputBoundary {

    private MenuItemViewModel viewModel;

    @Override
    public void present(GetMenuItemByIdResponseModel response) {
        this.viewModel = new MenuItemViewModel(
            response.id().toString(),
            response.name(),
            response.description(),
            response.price(),
            response.onlyLocal(),
            response.photoPath(),
            response.restaurantId().toString()
        );
    }

    public MenuItemViewModel getViewModel() {
        return viewModel;
    }
}
