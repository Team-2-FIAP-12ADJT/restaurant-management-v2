package com.fiap.restaurant_management_v2.application.usecases.user.create;

import com.fiap.restaurant_management_v2.application.exception.DuplicateUserException;
import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import com.fiap.restaurant_management_v2.application.gateways.PasswordEncoderGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;
import com.fiap.restaurant_management_v2.domain.User;

public class CreateUserInteractor implements CreateUserInputBoundary {

    private final UserDsGateway userDsGateway;
    private final PasswordEncoderGateway passwordEncoder;
    private final CreateUserOutputBoundary outputBoundary;
    private final LoggerGateway loggerGateway;

    public CreateUserInteractor(
        UserDsGateway userDsGateway,
        PasswordEncoderGateway passwordEncoder,
        CreateUserOutputBoundary outputBoundary,
        LoggerGateway loggerGateway
    ) {
        this.userDsGateway = userDsGateway;
        this.passwordEncoder = passwordEncoder;
        this.outputBoundary = outputBoundary;
        this.loggerGateway = loggerGateway;
    }

    @Override
    public void execute(CreateUserRequestModel request) {
        if (userDsGateway.existsByEmail(request.email())) {
            throw new DuplicateUserException("Email já cadastrado");
        }
        if (userDsGateway.existsByLogin(request.login())) {
            throw new DuplicateUserException("Login já cadastrado");
        }
        if (userDsGateway.existsByTaxIdentifier(request.taxIdentifier())) {
            throw new DuplicateUserException("CPF já cadastrado");
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        User user = User.create(
            request.name(),
            request.email(),
            request.login(),
            request.taxIdentifier(),
            encodedPassword
        );

        UserDsResponseModel saved = userDsGateway.save(
            new UserDsRequestModel(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getTaxIdentifier(),
                user.getPassword()
            )
        );

        loggerGateway.info("user created id={}", saved.id());

        outputBoundary.present(
            new CreateUserResponseModel(
                saved.id(),
                saved.name(),
                saved.email(),
                saved.login(),
                saved.taxIdentifier()
            )
        );
    }
}
