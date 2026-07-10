package com.fiap.restaurant_management_v2.application.usecases.restaurant.create;

import com.fiap.restaurant_management_v2.application.exception.DuplicateRestaurantException;
import com.fiap.restaurant_management_v2.application.exception.UserNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.TransactionalExecutor;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;
import com.fiap.restaurant_management_v2.domain.Restaurant;

public class CreateRestaurantInteractor
    implements CreateRestaurantInputBoundary
{

    private final TransactionalExecutor transactionalExecutor;
    private final RestaurantDsGateway restaurantDsGateway;
    private final UserDsGateway userDsGateway;
    private final CreateRestaurantOutputBoundary outputBoundary;
    private final LoggerGateway loggerGateway;

    public CreateRestaurantInteractor(
        TransactionalExecutor transactionalExecutor,
        RestaurantDsGateway restaurantDsGateway,
        UserDsGateway userDsGateway,
        CreateRestaurantOutputBoundary outputBoundary,
        LoggerGateway loggerGateway
    ) {
        this.transactionalExecutor = transactionalExecutor;
        this.restaurantDsGateway = restaurantDsGateway;
        this.userDsGateway = userDsGateway;
        this.outputBoundary = outputBoundary;
        this.loggerGateway = loggerGateway;
    }

    @Override
    public void execute(CreateRestaurantRequestModel request) {
        transactionalExecutor.execute(() -> {
            restaurantDsGateway
                .findByTaxIdentifier(request.taxIdentifier())
                .ifPresent(restaurant -> {
                    throw new DuplicateRestaurantException(
                        "Restaurant with tax identifier already exists"
                    );
                });

            UserDsResponseModel owner = userDsGateway
                .findById(request.ownerId())
                .orElseThrow(() ->
                    new UserNotFoundException(
                        "User not found with id: " + request.ownerId()
                    )
                );

            createRestaurant(request, owner);
        });
    }

    private void createRestaurant(
        CreateRestaurantRequestModel request,
        UserDsResponseModel owner
    ) {
        Restaurant restaurant = Restaurant.create(
            request.name(),
            request.address(),
            request.taxIdentifier(),
            request.cuisineType(),
            request.openingHours(),
            request.ownerId()
        );

        RestaurantDsResponseModel saved = restaurantDsGateway.save(
            new RestaurantDsRequestModel(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getTaxIdentifier(),
                restaurant.getCuisineType(),
                restaurant.getOpeningHours(),
                restaurant.getOwnerId()
            )
        );

        loggerGateway.info("restaurant created id={}", saved.id());

        outputBoundary.present(
            new CreateRestaurantResponseModel(
                saved.id(),
                saved.name(),
                saved.address(),
                saved.taxIdentifier(),
                saved.cuisineType(),
                saved.openingHours(),
                owner
            )
        );
    }
}
