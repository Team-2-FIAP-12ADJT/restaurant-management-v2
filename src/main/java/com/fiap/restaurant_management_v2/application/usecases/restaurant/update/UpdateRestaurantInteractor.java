package com.fiap.restaurant_management_v2.application.usecases.restaurant.update;

import com.fiap.restaurant_management_v2.application.exception.DuplicateUserException;
import com.fiap.restaurant_management_v2.application.exception.RestaurantNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.domain.Restaurant;
import java.util.UUID;

public class UpdateRestaurantInteractor implements UpdateRestaurantInputBoundary {

    private final RestaurantDsGateway restaurantDsGateway;
        private final UserDsGateway userDsGateway;
    private final UpdateRestaurantOutputBoundary outputBoundary;

    public UpdateRestaurantInteractor(
        RestaurantDsGateway restaurantDsGateway,
        UserDsGateway userDsGateway,
        UpdateRestaurantOutputBoundary outputBoundary
    ) {
        this.restaurantDsGateway = restaurantDsGateway;
        this.userDsGateway = userDsGateway;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(UpdateRestaurantRequestModel request) {
        UUID id = request.id();
        UUID ownerId = request.ownerId();

        if (!restaurantDsGateway.existsById(id)) {
            throw new RestaurantNotFoundException("Restaurante não encontrado: " + id);
        }

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
                id,
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getCuisineType(),
                restaurant.getOpeningHours(),
                restaurant.getOwnerId()
            )
        );

        outputBoundary.present(
            new UpdateRestaurantResponseModel(
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
