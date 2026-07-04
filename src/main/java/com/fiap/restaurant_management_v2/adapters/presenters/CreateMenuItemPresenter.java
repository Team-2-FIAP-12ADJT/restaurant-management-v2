package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.MenuItemViewModel;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.create.CreateMenuItemOutputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.create.CreateMenuItemResponseModel;

public class CreateMenuItemPresenter
    implements CreateMenuItemOutputBoundary {

    private MenuItemViewModel viewModel;

    @Override
    public void present(CreateMenuItemResponseModel response) {
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
