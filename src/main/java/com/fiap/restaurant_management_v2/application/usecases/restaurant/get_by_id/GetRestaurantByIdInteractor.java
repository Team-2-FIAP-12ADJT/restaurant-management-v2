package com.fiap.restaurant_management_v2.application.usecases.restaurant.get_by_id;

import com.fiap.restaurant_management_v2.application.exception.RestaurantNotFoundException;
import com.fiap.restaurant_management_v2.application.exception.UserNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;

public class GetRestaurantByIdInteractor
    implements GetRestaurantByIdInputBoundary
{

    private final RestaurantDsGateway restaurantDsGateway;
    private final UserDsGateway userDsGateway;
    private final GetRestaurantByIdOutputBoundary outputBoundary;

    public GetRestaurantByIdInteractor(
        RestaurantDsGateway restaurantDsGateway,
        UserDsGateway userDsGateway,
        GetRestaurantByIdOutputBoundary outputBoundary
    ) {
        this.restaurantDsGateway = restaurantDsGateway;
        this.userDsGateway = userDsGateway;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(GetRestaurantByIdRequestModel request) {
        RestaurantDsResponseModel response = restaurantDsGateway
            .findById(request.id())
            .orElseThrow(() ->
                new RestaurantNotFoundException(
                    "Restaurante não encontrado: " + request.id()
                )
            );

        UserDsResponseModel owner = userDsGateway
            .findById(response.ownerId())
            .orElseThrow(() ->
                new UserNotFoundException(
                    "Owner not found with id: " + response.ownerId()
                )
            );

        outputBoundary.present(
            new GetRestaurantByIdResponseModel(
                response.id(),
                response.name(),
                response.address(),
                response.taxIdentifier(),
                response.cuisineType(),
                response.openingHours(),
                owner
            )
        );
    }
}
