package com.fiap.restaurant_management_v2.application.usecases.user.delete;

import com.fiap.restaurant_management_v2.application.exception.UserHasActiveRestaurantsException;
import com.fiap.restaurant_management_v2.application.exception.UserNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;

public class DeleteUserByIdInteractor implements DeleteUserByIdInputBoundary {

    private final UserDsGateway userDsGateway;
    private final RestaurantDsGateway restaurantDsGateway;
    private final DeleteUserByIdOutputBoundary outputBoundary;

    public DeleteUserByIdInteractor(
        UserDsGateway userDsGateway,
        RestaurantDsGateway restaurantDsGateway,
        DeleteUserByIdOutputBoundary outputBoundary
    ) {
        this.userDsGateway = userDsGateway;
        this.restaurantDsGateway = restaurantDsGateway;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(DeleteUserByIdRequestModel request) {
        UserDsResponseModel user = userDsGateway
            .findById(request.id())
            .orElseThrow(() ->
                new UserNotFoundException(
                    "User not found with id: " + request.id()
                )
            );

        // Guarda: mantém o invariante "restaurante tem dono" — não orfana.
        if (restaurantDsGateway.existsByOwnerIdAndIsActive(user.id())) {
            throw new UserHasActiveRestaurantsException(
                "Não é possível deletar o usuário: possui restaurante ativo"
            );
        }

        userDsGateway.deleteById(user.id());
        outputBoundary.present();
    }
}
