package com.fiap.restaurant_management_v2.application.usecases.restaurant.get_by_id;

import com.fiap.restaurant_management_v2.application.exception.RestaurantNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsResponseModel;

public class GetRestaurantByIdInteractor implements GetRestaurantByIdInputBoundary {

    private final RestaurantDsGateway restaurantDsGateway;
    private final GetRestaurantByIdOutputBoundary outputBoundary;

    public GetRestaurantByIdInteractor(
        RestaurantDsGateway restaurantDsGateway,
        GetRestaurantByIdOutputBoundary outputBoundary
    ) {
        this.restaurantDsGateway = restaurantDsGateway;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(GetRestaurantByIdRequestModel request) {
        RestaurantDsResponseModel response = restaurantDsGateway
            .findById(request.id())
            .orElseThrow(() ->
                new RestaurantNotFoundException("Restaurante não encontrado: " + request.id())
            );

        outputBoundary.present(
            new GetRestaurantByIdResponseModel(
                response.id(),
                response.name(),
                response.address(),
                response.cuisineType(),
                response.openingHours(),
                response.ownerId()
            )
        );
    }
}
