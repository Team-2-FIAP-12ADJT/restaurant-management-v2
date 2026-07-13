package com.fiap.restaurant_management_v2.application.usecases.restaurant.delete;

import com.fiap.restaurant_management_v2.application.exception.RestaurantHasActiveMenuItemsException;
import com.fiap.restaurant_management_v2.application.exception.RestaurantNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;

public class DeleteRestaurantInteractor implements DeleteRestaurantInputBoundary {

    private final RestaurantDsGateway restaurantDsGateway;
    private final MenuItemDsGateway menuItemDsGateway;
    private final DeleteRestaurantOutputBoundary outputBoundary;
    private final LoggerGateway loggerGateway;

    public DeleteRestaurantInteractor(
        RestaurantDsGateway restaurantDsGateway,
        MenuItemDsGateway menuItemDsGateway,
        DeleteRestaurantOutputBoundary outputBoundary,
        LoggerGateway loggerGateway
    ) {
        this.restaurantDsGateway = restaurantDsGateway;
        this.menuItemDsGateway = menuItemDsGateway;
        this.outputBoundary = outputBoundary;
        this.loggerGateway = loggerGateway;
    }

    @Override
    public void execute(DeleteRestaurantRequestModel request) {
        if (!restaurantDsGateway.existsById(request.id())) {
            throw new RestaurantNotFoundException("Restaurante não encontrado: " + request.id());
        }

        if (menuItemDsGateway.existsByRestaurantIdAndIsActive(request.id())) {
            loggerGateway.warn(
                "restaurant delete blocked: active menu items restaurantId={}",
                request.id()
            );
            throw new RestaurantHasActiveMenuItemsException(
                "Não é possível deletar o restaurante: possui itens do cardápio ativo"
            );
        }

        restaurantDsGateway.deleteById(request.id());
        loggerGateway.info("restaurant deleted id={}", request.id());
        outputBoundary.present(new DeleteRestaurantResponseModel(request.id()));
    }
}
