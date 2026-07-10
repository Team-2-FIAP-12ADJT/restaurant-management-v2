package com.fiap.restaurant_management_v2.application.usecases.usertype.create;

import com.fiap.restaurant_management_v2.application.exception.DuplicateUserTypeException;
import com.fiap.restaurant_management_v2.application.gateways.*;
import com.fiap.restaurant_management_v2.domain.UserType;

public class CreateUserTypeInteractor implements CreateUserTypeInputBoundary {

    private final UserTypeDsGateway userTypeDsGateway;
    private final CreateUserTypeOutputBoundary createUserTypeOutputBoundary ;
    private final LoggerGateway loggerGateway;

    public CreateUserTypeInteractor(UserTypeDsGateway userTypeDsGateway,
                                    CreateUserTypeOutputBoundary createUserTypeOutputBoundary,
                                    LoggerGateway loggerGateway
    ) {
        this.userTypeDsGateway = userTypeDsGateway;
        this.createUserTypeOutputBoundary = createUserTypeOutputBoundary;
        this.loggerGateway = loggerGateway;
    }

    @Override
    public void execute(CreateUserTypeRequestModel request) {
        if (userTypeDsGateway.existsByUserType(request.userType())) {
            throw new DuplicateUserTypeException("Tipo de usuário já cadastrado");
        }


        UserType userType = UserType.create(request.userType());

        UserTypeDsResponseModel saved = userTypeDsGateway.save(
                new UserTypeDsRequestModel(
                        userType.getId(),
                        userType.getUserType()
                )
        );

        loggerGateway.info("user type created id={}", saved.id());

        createUserTypeOutputBoundary.present(
                new CreateUserTypeResponseModel(
                        saved.id() ,
                        saved.userType()
                )
        );

    }



}
