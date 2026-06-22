package com.fiap.restaurant_management_v2.application.usecases.restaurant.delete;

import com.fiap.restaurant_management_v2.application.exception.RestaurantNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;

public class DeleteRestaurantInteractor implements DeleteRestaurantInputBoundary {

    private final RestaurantDsGateway restaurantDsGateway;
    private final DeleteRestaurantOutputBoundary outputBoundary;

    public DeleteRestaurantInteractor(
        RestaurantDsGateway restaurantDsGateway,
        DeleteRestaurantOutputBoundary outputBoundary
    ) {
        this.restaurantDsGateway = restaurantDsGateway;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(DeleteRestaurantRequestModel request) {
        if (!restaurantDsGateway.existsById(request.id())) {
            throw new RestaurantNotFoundException("Restaurante não encontrado: " + request.id());
        }

        restaurantDsGateway.deleteById(request.id());
        outputBoundary.present(new DeleteRestaurantResponseModel(request.id()));
    }
}
