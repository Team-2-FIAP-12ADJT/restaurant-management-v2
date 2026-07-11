package com.fiap.restaurant_management_v2.application.usecases.menuitem.create;

import com.fiap.restaurant_management_v2.application.exception.RestaurantNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.TransactionalExecutor;
import com.fiap.restaurant_management_v2.domain.MenuItem;
import java.util.UUID;

public class CreateMenuItemInteractor implements CreateMenuItemInputBoundary {

    private final TransactionalExecutor transactionalExecutor;
    private final MenuItemDsGateway menuItemDsGateway;
    private final RestaurantDsGateway restaurantDsGateway;
    private final CreateMenuItemOutputBoundary outputBoundary;
    private final LoggerGateway loggerGateway;

    public CreateMenuItemInteractor(
        TransactionalExecutor transactionalExecutor,
        MenuItemDsGateway menuItemDsGateway,
        RestaurantDsGateway restaurantDsGateway,
        CreateMenuItemOutputBoundary outputBoundary,
        LoggerGateway loggerGateway
    ) {
        this.transactionalExecutor = transactionalExecutor;
        this.menuItemDsGateway = menuItemDsGateway;
        this.restaurantDsGateway = restaurantDsGateway;
        this.outputBoundary = outputBoundary;
        this.loggerGateway = loggerGateway;
    }

    @Override
    public void execute(CreateMenuItemRequestModel request) {
        transactionalExecutor.execute(() -> {
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

            loggerGateway.info(
                "menu item created id={} restaurantId={}",
                saved.id(),
                saved.restaurantId()
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
        });
    }
}
