package com.fiap.restaurant_management_v2.application.usecases.user.delete;

import com.fiap.restaurant_management_v2.application.exception.UserNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;

public class DeleteUserByIdInteractor implements DeleteUserByIdInputBoundary {

    private final UserDsGateway userDsGateway;
    private final DeleteUserByIdOutputBoundary outputBoundary;

    public DeleteUserByIdInteractor(
            UserDsGateway userDsGateway,
            DeleteUserByIdOutputBoundary outputBoundary
    ) {
        this.userDsGateway = userDsGateway;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(DeleteUserByIdRequestModel request) {
        UserDsResponseModel user = userDsGateway
                .findByIdAndDeletedAtIsNull(request.id())
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + request.id()
                        )
                );

        userDsGateway.deleteById(user.id());
        outputBoundary.present();
    }
}