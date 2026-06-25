package com.fiap.restaurant_management_v2.application.usecases.usertype.delete;

import com.fiap.restaurant_management_v2.application.exception.UserTypeNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsResponseModel;

public class DeleteUserTypeByIdInteractor implements DeleteUserTypeByIdInputBoundary {

    private final UserTypeDsGateway userTypeDsGateway;
    private final UserDsGateway userDsGateway;
    private final DeleteUserTypeByIdOutputBoundary outputBoundary;

    public DeleteUserTypeByIdInteractor(
            UserTypeDsGateway userTypeDsGateway,
            UserDsGateway userDsGateway,
            DeleteUserTypeByIdOutputBoundary outputBoundary
    ) {
        this.userTypeDsGateway = userTypeDsGateway;
        this.userDsGateway = userDsGateway;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(DeleteUserTypeByIdRequestModel request) {
        UserTypeDsResponseModel userType = userTypeDsGateway
                .findById(request.id())
                .orElseThrow(() ->
                        new UserTypeNotFoundException(
                                "User type not found with id: " + request.id()
                        )
                );

        userDsGateway.unbindUserType(userType.id());
        userTypeDsGateway.deleteById(userType.id());
        outputBoundary.present();
    }
}
