package com.fiap.restaurant_management_v2.application.usecases.user.get_user_by_id;

import com.fiap.restaurant_management_v2.application.exception.UserNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;

public class GetUserByIdInteractor implements GetUserByIdInputBoundary {

    private final UserDsGateway userDsGateway;
    private final GetUserByIdOutputBoundary outputBoundary;

    public GetUserByIdInteractor(
        UserDsGateway userDsGateway,
        GetUserByIdOutputBoundary outputBoundary
    ) {
        this.userDsGateway = userDsGateway;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(GetUserByIdRequestModel request) {
        UserDsResponseModel user = userDsGateway
            .findById(request.id())
            .orElseThrow(() ->
                new UserNotFoundException("User not found: " + request.id())
            );

        var response = new GetUserByIdResponseModel(
            user.id(),
            user.name(),
            user.email(),
            user.login(),
            user.taxIdentifier()
        );
        outputBoundary.present(response);
    }
}
