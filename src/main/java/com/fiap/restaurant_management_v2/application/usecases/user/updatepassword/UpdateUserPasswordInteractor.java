package com.fiap.restaurant_management_v2.application.usecases.user.updatepassword;

import com.fiap.restaurant_management_v2.application.exception.IncorrectPasswordException;
import com.fiap.restaurant_management_v2.application.exception.SamePasswordException;
import com.fiap.restaurant_management_v2.application.exception.UserNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import com.fiap.restaurant_management_v2.application.gateways.PasswordEncoderGateway;
import com.fiap.restaurant_management_v2.application.gateways.TransactionalExecutor;
import com.fiap.restaurant_management_v2.application.gateways.UserCredentialDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;

public class UpdateUserPasswordInteractor implements UpdateUserPasswordInputBoundary {

    private final UserDsGateway userDsGateway;
    private final PasswordEncoderGateway passwordEncoder;
    private final TransactionalExecutor transactionalExecutor;
    private final UpdateUserPasswordOutputBoundary outputBoundary;
    private final LoggerGateway loggerGateway;

    public UpdateUserPasswordInteractor(
        UserDsGateway userDsGateway,
        PasswordEncoderGateway passwordEncoder,
        TransactionalExecutor transactionalExecutor,
        UpdateUserPasswordOutputBoundary outputBoundary,
        LoggerGateway loggerGateway
    ) {
        this.userDsGateway = userDsGateway;
        this.passwordEncoder = passwordEncoder;
        this.transactionalExecutor = transactionalExecutor;
        this.outputBoundary = outputBoundary;
        this.loggerGateway = loggerGateway;
    }

    @Override
    public void execute(UpdateUserPasswordRequestModel request) {
        transactionalExecutor.execute(() -> {
            UserCredentialDsResponseModel credential = userDsGateway
                .findCredentialById(request.id())
                .orElseThrow(() ->
                    new UserNotFoundException("User not found with id: " + request.id())
                );

            if (!passwordEncoder.matches(request.oldPassword(), credential.passwordHash())) {
                throw new IncorrectPasswordException("Old password is incorrect");
            }

            if (passwordEncoder.matches(request.newPassword(), credential.passwordHash())) {
                throw new SamePasswordException("New password cannot be the same as the current password");
            }

            String encodedNewPassword = passwordEncoder.encode(request.newPassword());

            userDsGateway.updatePassword(request.id(), encodedNewPassword);
            loggerGateway.info("password updated for user id={}", request.id());
        });

        outputBoundary.present();
    }
}
