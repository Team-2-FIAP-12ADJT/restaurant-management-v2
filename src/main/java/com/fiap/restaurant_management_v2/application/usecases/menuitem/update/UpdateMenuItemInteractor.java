package com.fiap.restaurant_management_v2.application.usecases.menuitem.update;

import com.fiap.restaurant_management_v2.application.exception.MenuItemNotFoundException;
import com.fiap.restaurant_management_v2.application.exception.RestaurantNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.domain.MenuItem;
import java.util.UUID;

public class UpdateMenuItemInteractor implements UpdateMenuItemInputBoundary {

    private final MenuItemDsGateway menuItemDsGateway;
    private final RestaurantDsGateway restaurantDsGateway;
    private final UpdateMenuItemOutputBoundary outputBoundary;
    private final LoggerGateway loggerGateway;

    public UpdateMenuItemInteractor(
        MenuItemDsGateway menuItemDsGateway,
        RestaurantDsGateway restaurantDsGateway,
        UpdateMenuItemOutputBoundary outputBoundary,
        LoggerGateway loggerGateway
    ) {
        this.menuItemDsGateway = menuItemDsGateway;
        this.restaurantDsGateway = restaurantDsGateway;
        this.outputBoundary = outputBoundary;
        this.loggerGateway = loggerGateway;
    }

    @Override
    public void execute(UpdateMenuItemRequestModel request) {
        UUID id = request.id();
        UUID restaurantId = request.restaurantId();

        if (!menuItemDsGateway.existsById(id)) {
            throw new MenuItemNotFoundException(
                "Item do cardápio não encontrado: " + id
            );
        }

        if (!restaurantDsGateway.existsById(restaurantId)) {
            throw new RestaurantNotFoundException(
                "Restaurante não encontrado: " + restaurantId
            );
        }

        MenuItem menuItem = MenuItem.create(
            request.name(),
            request.description(),
            request.price(),
            request.onlyLocal(),
            request.photoPath(),
            restaurantId
        );

        MenuItemDsResponseModel saved = menuItemDsGateway.save(
            new MenuItemDsRequestModel(
                id,
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.isOnlyLocal(),
                menuItem.getPhotoPath(),
                menuItem.getRestaurantId()
            )
        );

        loggerGateway.info("menu item updated id={}", saved.id());

        outputBoundary.present(
            new UpdateMenuItemResponseModel(
                saved.id(),
                saved.name(),
                saved.description(),
                saved.price(),
                saved.onlyLocal(),
                saved.photoPath(),
                saved.restaurantId()
            )
        );
    }
}
