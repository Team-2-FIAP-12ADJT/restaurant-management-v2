package com.fiap.restaurant_management_v2.application.usecases.usertype.delete;

import com.fiap.restaurant_management_v2.application.exception.UserTypeNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import com.fiap.restaurant_management_v2.application.gateways.TransactionalExecutor;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsResponseModel;

public class DeleteUserTypeByIdInteractor implements DeleteUserTypeByIdInputBoundary {

    private final UserTypeDsGateway userTypeDsGateway;
    private final UserDsGateway userDsGateway;
    private final TransactionalExecutor transactionalExecutor;
    private final DeleteUserTypeByIdOutputBoundary outputBoundary;
    private final LoggerGateway loggerGateway;

    public DeleteUserTypeByIdInteractor(
            UserTypeDsGateway userTypeDsGateway,
            UserDsGateway userDsGateway,
            TransactionalExecutor transactionalExecutor,
            DeleteUserTypeByIdOutputBoundary outputBoundary,
            LoggerGateway loggerGateway
    ) {
        this.userTypeDsGateway = userTypeDsGateway;
        this.userDsGateway = userDsGateway;
        this.transactionalExecutor = transactionalExecutor;
        this.outputBoundary = outputBoundary;
        this.loggerGateway = loggerGateway;
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

        transactionalExecutor.execute(() -> {
            userDsGateway.unbindUserType(userType.id());
            userTypeDsGateway.deleteById(userType.id());
        });
        loggerGateway.info("user type deleted id={}", userType.id());
        outputBoundary.present();
    }
}
