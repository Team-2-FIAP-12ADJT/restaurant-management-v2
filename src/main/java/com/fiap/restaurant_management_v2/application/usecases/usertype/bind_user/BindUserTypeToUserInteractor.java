package com.fiap.restaurant_management_v2.application.usecases.usertype.bind_user;

import com.fiap.restaurant_management_v2.application.exception.UserNotFoundException;
import com.fiap.restaurant_management_v2.application.exception.UserTypeNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsGateway;

public class BindUserTypeToUserInteractor implements BindUserTypeToUserInputBoundary {

    private final UserTypeDsGateway userTypeDsGateway;
    private final UserDsGateway userDsGateway ;
    private final LoggerGateway loggerGateway;

    public BindUserTypeToUserInteractor(
            UserTypeDsGateway userTypeDsGateway ,
            UserDsGateway userDsGateway,
            LoggerGateway loggerGateway
    ) {
        this.userTypeDsGateway = userTypeDsGateway;
        this.userDsGateway = userDsGateway;
        this.loggerGateway = loggerGateway;
    }

    @Override
    public void execute(BindUserTypeToUserRequestModel request) {
        userDsGateway.findAllById(request.userId())
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        userTypeDsGateway.findById(request.typeId())
                .orElseThrow(() -> new UserTypeNotFoundException("Tipo de usuário não encontrado"));

        userDsGateway.bindUserType(request.userId(), request.typeId());
        loggerGateway.info(
                "user type bound userId={} typeId={}",
                request.userId(),
                request.typeId()
        );
    }
}
