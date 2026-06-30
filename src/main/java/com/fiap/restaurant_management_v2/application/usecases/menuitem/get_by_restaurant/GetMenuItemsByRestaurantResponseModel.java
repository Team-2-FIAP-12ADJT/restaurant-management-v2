package com.fiap.restaurant_management_v2.application.usecases.menuitem.get_by_restaurant;

import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_all.MenuItemSummary;

public record GetMenuItemsByRestaurantResponseModel(
    PageResult<MenuItemSummary> page
) {}
