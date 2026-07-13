package com.fiap.restaurant_management_v2.application.usecases.user.get_user_by_id;

import com.fiap.restaurant_management_v2.application.exception.UserNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;
import java.util.List;

public class GetUserByIdInteractor implements GetUserByIdInputBoundary {

    private final UserDsGateway userDsGateway;
    private final RestaurantDsGateway restaurantDsGateway;
    private final GetUserByIdOutputBoundary outputBoundary;

    public GetUserByIdInteractor(
        UserDsGateway userDsGateway,
        RestaurantDsGateway restaurantDsGateway,
        GetUserByIdOutputBoundary outputBoundary
    ) {
        this.userDsGateway = userDsGateway;
        this.restaurantDsGateway = restaurantDsGateway;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(GetUserByIdRequestModel request) {
        UserDsResponseModel user = userDsGateway
            .findById(request.id())
            .orElseThrow(() ->
                new UserNotFoundException("User not found: " + request.id())
            );

        var restaurants = restaurantDsGateway.findAllByOwnerIds(List.of(user.id()));

        var response = new GetUserByIdResponseModel(
            user.id(),
            user.name(),
            user.email(),
            user.login(),
            user.taxIdentifier(),
            user.userTypeName(),
            restaurants
        );
        outputBoundary.present(response);
    }
}
