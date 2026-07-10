package com.fiap.restaurant_management_v2.application.usecases.usertype.update;

import com.fiap.restaurant_management_v2.application.exception.DuplicateUserTypeException;
import com.fiap.restaurant_management_v2.application.exception.UserTypeNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsResponseModel;
import com.fiap.restaurant_management_v2.domain.UserType;

public class UpdateUserTypeInteractor implements UpdateUserTypeInputBoundary {

    private final UserTypeDsGateway userTypeDsGateway;
    private final UpdateUserTypeOutputBoundary outputBoundary;
    private final LoggerGateway loggerGateway;

    public UpdateUserTypeInteractor(
        UserTypeDsGateway userTypeDsGateway,
        UpdateUserTypeOutputBoundary outputBoundary,
        LoggerGateway loggerGateway
    ) {
        this.userTypeDsGateway = userTypeDsGateway;
        this.outputBoundary = outputBoundary;
        this.loggerGateway = loggerGateway;
    }

    @Override
    public void execute(UpdateUserTypeRequestModel request) {
        UserTypeDsResponseModel existingData = userTypeDsGateway
            .findById(request.id())
            .orElseThrow(() ->
                new UserTypeNotFoundException("Tipo de usuário não encontrado")
            );

        if (
            !existingData.userType().equals(request.userType()) &&
            userTypeDsGateway.existsByUserType(request.userType())
        ) {
            throw new DuplicateUserTypeException("Tipo de usuário já existe");
        }

        UserType userType = UserType.restore(
            existingData.id(),
            existingData.userType()
        );

        UserType updateType = userType.changeType(request.userType());

        UserTypeDsResponseModel update = userTypeDsGateway.save(
            new UserTypeDsRequestModel(
                updateType.getId(),
                updateType.getUserType()
            )
        );

        loggerGateway.info("user type updated id={}", update.id());

        outputBoundary.present(
            new UpdateUserTypeResponseModel(update.id(), update.userType())
        );
    }
}
