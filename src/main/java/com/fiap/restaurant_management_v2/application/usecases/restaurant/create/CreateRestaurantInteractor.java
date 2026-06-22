package com.fiap.restaurant_management_v2.application.usecases.restaurant.create;

import com.fiap.restaurant_management_v2.application.exception.DuplicateUserException;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.domain.Restaurant;
import java.util.UUID;

public class CreateRestaurantInteractor implements CreateRestaurantInputBoundary {

    private final RestaurantDsGateway restaurantDsGateway;
    private final UserDsGateway userDsGateway;
    private final CreateRestaurantOutputBoundary outputBoundary;

    public CreateRestaurantInteractor(
        RestaurantDsGateway restaurantDsGateway,
        UserDsGateway userDsGateway,
        CreateRestaurantOutputBoundary outputBoundary
    ) {
        this.restaurantDsGateway = restaurantDsGateway;
        this.userDsGateway = userDsGateway;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(CreateRestaurantRequestModel request) {
        UUID ownerId = request.ownerId();

        if (!userDsGateway.existsById(ownerId)) {
            throw new DuplicateUserException("Usuário dono do restaurante não encontrado");
        }

        Restaurant restaurant = Restaurant.create(
            request.name(),
            request.address(),
            request.cuisineType(),
            request.openingHours(),
            ownerId
        );

        RestaurantDsResponseModel saved = restaurantDsGateway.save(
            new RestaurantDsRequestModel(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getCuisineType(),
                restaurant.getOpeningHours(),
                restaurant.getOwnerId()
            )
        );

        outputBoundary.present(
            new CreateRestaurantResponseModel(
                saved.id(),
                saved.name(),
                saved.address(),
                saved.cuisineType(),
                saved.openingHours(),
                saved.ownerId()
            )
        );
    }
}
