package com.fiap.restaurant_management_v2.application.usecases.user.update;

import com.fiap.restaurant_management_v2.application.exception.DuplicateUserException;
import com.fiap.restaurant_management_v2.application.exception.UserNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.TransactionalExecutor;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;
import com.fiap.restaurant_management_v2.domain.User;

public class UpdateUserInteractor implements UpdateUserInputBoundary {

    private final UserDsGateway userDsGateway;
    private final TransactionalExecutor transactionalExecutor;
    private final UpdateUserOutputBoundary outputBoundary;

    public UpdateUserInteractor(
        UserDsGateway userDsGateway,
        TransactionalExecutor transactionalExecutor,
        UpdateUserOutputBoundary outputBoundary
    ) {
        this.userDsGateway = userDsGateway;
        this.transactionalExecutor = transactionalExecutor;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(UpdateUserRequestModel request) {
        transactionalExecutor.execute(() -> {
            UserDsResponseModel current = userDsGateway
                .findById(request.id())
                .orElseThrow(() ->
                    new UserNotFoundException(
                        "User not found with id: " + request.id()
                    )
                );

            String name =
                request.name() != null ? request.name() : current.name();
            String email =
                request.email() != null ? request.email() : current.email();
            String login =
                request.login() != null ? request.login() : current.login();
            String taxIdentifier =
                request.taxIdentifier() != null
                    ? request.taxIdentifier()
                    : current.taxIdentifier();

            if (
                !email.equals(current.email()) &&
                userDsGateway.existsByEmailExcludingId(email, request.id())
            ) {
                throw new DuplicateUserException("Email já cadastrado");
            }
            if (
                !login.equals(current.login()) &&
                userDsGateway.existsByLoginExcludingId(login, request.id())
            ) {
                throw new DuplicateUserException("Login já cadastrado");
            }
            if (
                !taxIdentifier.equals(current.taxIdentifier()) &&
                userDsGateway.existsByTaxIdentifierExcludingId(
                    taxIdentifier,
                    request.id()
                )
            ) {
                throw new DuplicateUserException("CPF já cadastrado");
            }

            User.validateDetails(name, email, login, taxIdentifier);

            UserDsResponseModel saved = userDsGateway.update(
                request.id(),
                name,
                email,
                login,
                taxIdentifier
            );

            outputBoundary.present(
                new UpdateUserResponseModel(
                    saved.id(),
                    saved.name(),
                    saved.email(),
                    saved.login(),
                    saved.taxIdentifier()
                )
            );
        });
    }
}
