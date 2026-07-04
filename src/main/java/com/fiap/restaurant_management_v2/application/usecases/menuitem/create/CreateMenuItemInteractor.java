package com.fiap.restaurant_management_v2.application.usecases.menuitem.create;

import com.fiap.restaurant_management_v2.application.exception.RestaurantNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.domain.MenuItem;
import java.util.UUID;

public class CreateMenuItemInteractor implements CreateMenuItemInputBoundary {

    private final MenuItemDsGateway menuItemDsGateway;
    private final RestaurantDsGateway restaurantDsGateway;
    private final CreateMenuItemOutputBoundary outputBoundary;

    public CreateMenuItemInteractor(
        MenuItemDsGateway menuItemDsGateway,
        RestaurantDsGateway restaurantDsGateway,
        CreateMenuItemOutputBoundary outputBoundary
    ) {
        this.menuItemDsGateway = menuItemDsGateway;
        this.restaurantDsGateway = restaurantDsGateway;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(CreateMenuItemRequestModel request) {
        UUID restaurantId = request.restaurantId();

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
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.isOnlyLocal(),
                menuItem.getPhotoPath(),
                menuItem.getRestaurantId()
            )
        );

        outputBoundary.present(
            new CreateMenuItemResponseModel(
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
