package com.fiap.restaurant_management_v2.application.usecases.menuitem.delete;

import com.fiap.restaurant_management_v2.application.exception.MenuItemNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsGateway;

public class DeleteMenuItemInteractor implements DeleteMenuItemInputBoundary {

    private final MenuItemDsGateway menuItemDsGateway;
    private final DeleteMenuItemOutputBoundary outputBoundary;
    private final LoggerGateway loggerGateway;

    public DeleteMenuItemInteractor(
        MenuItemDsGateway menuItemDsGateway,
        DeleteMenuItemOutputBoundary outputBoundary,
        LoggerGateway loggerGateway
    ) {
        this.menuItemDsGateway = menuItemDsGateway;
        this.outputBoundary = outputBoundary;
        this.loggerGateway = loggerGateway;
    }

    @Override
    public void execute(DeleteMenuItemRequestModel request) {
        if (!menuItemDsGateway.existsById(request.id())) {
            throw new MenuItemNotFoundException(
                "Item do cardápio não encontrado: " + request.id()
            );
        }

        menuItemDsGateway.deleteById(request.id());
        loggerGateway.info("menu item deleted id={}", request.id());
        outputBoundary.present(new DeleteMenuItemResponseModel(request.id()));
    }
}
