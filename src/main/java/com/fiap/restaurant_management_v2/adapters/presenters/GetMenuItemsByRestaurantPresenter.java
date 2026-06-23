package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.MenuItemViewModel;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.PageViewModel;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_by_restaurant.GetMenuItemsByRestaurantOutputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_by_restaurant.GetMenuItemsByRestaurantResponseModel;

public class GetMenuItemsByRestaurantPresenter
    implements GetMenuItemsByRestaurantOutputBoundary {

    private PageViewModel<MenuItemViewModel> viewModel;

    @Override
    public void present(GetMenuItemsByRestaurantResponseModel response) {
        this.viewModel = GetAllMenuItemsPresenter.toViewModel(response.page());
    }

    public PageViewModel<MenuItemViewModel> getViewModel() {
        return viewModel;
    }
}
