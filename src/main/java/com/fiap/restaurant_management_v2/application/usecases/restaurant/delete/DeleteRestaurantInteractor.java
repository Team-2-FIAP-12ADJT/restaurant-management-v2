package com.fiap.restaurant_management_v2.application.usecases.restaurant.delete;

import com.fiap.restaurant_management_v2.application.exception.RestaurantNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;

public class DeleteRestaurantInteractor implements DeleteRestaurantInputBoundary {

    private final RestaurantDsGateway restaurantDsGateway;
    private final DeleteRestaurantOutputBoundary outputBoundary;
    private final LoggerGateway loggerGateway;

    public DeleteRestaurantInteractor(
        RestaurantDsGateway restaurantDsGateway,
        DeleteRestaurantOutputBoundary outputBoundary,
        LoggerGateway loggerGateway
    ) {
        this.restaurantDsGateway = restaurantDsGateway;
        this.outputBoundary = outputBoundary;
        this.loggerGateway = loggerGateway;
    }

    @Override
    public void execute(DeleteRestaurantRequestModel request) {
        if (!restaurantDsGateway.existsById(request.id())) {
            throw new RestaurantNotFoundException("Restaurante não encontrado: " + request.id());
        }

        restaurantDsGateway.deleteById(request.id());
        loggerGateway.info("restaurant deleted id={}", request.id());
        outputBoundary.present(new DeleteRestaurantResponseModel(request.id()));
    }
}
