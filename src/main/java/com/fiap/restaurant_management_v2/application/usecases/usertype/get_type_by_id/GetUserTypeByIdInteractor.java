package com.fiap.restaurant_management_v2.application.usecases.usertype.get_type_by_id;

import com.fiap.restaurant_management_v2.application.exception.UserTypeNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsResponseModel;

public class GetUserTypeByIdInteractor implements GetUserTypeByIdInputBoundary {

    private final UserTypeDsGateway userTypeDsGateway;
    private final GetUserTypeByIdOutputBoundary outputBoundary;

    public GetUserTypeByIdInteractor(
            UserTypeDsGateway userTypeDsGateway,
            GetUserTypeByIdOutputBoundary outputBoundary) {

        this.userTypeDsGateway = userTypeDsGateway;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(GetUserTypeByIdRequestModel request) {
        UserTypeDsResponseModel userType = userTypeDsGateway
            .findById(request.id())
            .orElseThrow(() ->
                new UserTypeNotFoundException("User Type not found: " + request.id())
            );

        var response = new GetUserTypeByIdResponseModel(
                userType.id(),
                userType.userType()
        );
        outputBoundary.present(response);
    }
}
