package com.fiap.restaurant_management_v2.application.usecases.menuitem.get_all;

public record GetAllMenuItemsRequestModel(
    String name,
    int page,
    int size
) {}
