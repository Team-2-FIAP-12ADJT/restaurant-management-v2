package com.fiap.restaurant_management_v2.application.usecases.menuitem.get_by_id;

import com.fiap.restaurant_management_v2.application.exception.MenuItemNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsResponseModel;

public class GetMenuItemByIdInteractor implements GetMenuItemByIdInputBoundary {

    private final MenuItemDsGateway menuItemDsGateway;
    private final GetMenuItemByIdOutputBoundary outputBoundary;

    public GetMenuItemByIdInteractor(
        MenuItemDsGateway menuItemDsGateway,
        GetMenuItemByIdOutputBoundary outputBoundary
    ) {
        this.menuItemDsGateway = menuItemDsGateway;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(GetMenuItemByIdRequestModel request) {
        MenuItemDsResponseModel response = menuItemDsGateway
            .findById(request.id())
            .orElseThrow(() ->
                new MenuItemNotFoundException(
                    "Item do cardápio não encontrado: " + request.id()
                )
            );

        outputBoundary.present(
            new GetMenuItemByIdResponseModel(
                response.id(),
                response.name(),
                response.description(),
                response.price(),
                response.onlyLocal(),
                response.photoPath(),
                response.restaurantId()
            )
        );
    }
}
