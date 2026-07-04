package com.fiap.restaurant_management_v2.application.usecases.menuitem.get_all;

import com.fiap.restaurant_management_v2.application.pagination.PageResult;

public record GetAllMenuItemsResponseModel(PageResult<MenuItemSummary> page) {}
