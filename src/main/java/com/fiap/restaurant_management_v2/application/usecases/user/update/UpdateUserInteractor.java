package com.fiap.restaurant_management_v2.application.usecases.user.update;

import com.fiap.restaurant_management_v2.application.exception.DuplicateUserException;
import com.fiap.restaurant_management_v2.application.exception.UserNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import com.fiap.restaurant_management_v2.application.gateways.TransactionalExecutor;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;
import com.fiap.restaurant_management_v2.domain.User;
import java.util.UUID;

public class UpdateUserInteractor implements UpdateUserInputBoundary {

    private final UserDsGateway userDsGateway;
    private final TransactionalExecutor transactionalExecutor;
    private final UpdateUserOutputBoundary outputBoundary;
    private final LoggerGateway loggerGateway;

    public UpdateUserInteractor(
        UserDsGateway userDsGateway,
        TransactionalExecutor transactionalExecutor,
        UpdateUserOutputBoundary outputBoundary,
        LoggerGateway loggerGateway
    ) {
        this.userDsGateway = userDsGateway;
        this.transactionalExecutor = transactionalExecutor;
        this.outputBoundary = outputBoundary;
        this.loggerGateway = loggerGateway;
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

            String name = merge(request.name(), current.name());
            String email = merge(request.email(), current.email());
            String login = merge(request.login(), current.login());
            String taxIdentifier = merge(
                request.taxIdentifier(),
                current.taxIdentifier()
            );

            ensureNoDuplicates(request.id(), current, email, login, taxIdentifier);

            User.validateDetails(name, email, login, taxIdentifier);

            UserDsResponseModel saved = userDsGateway.update(
                request.id(),
                name,
                email,
                login,
                taxIdentifier
            );

            loggerGateway.info("user updated id={}", saved.id());

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

    // PATCH parcial: campo ausente (null) = mantém o valor atual.
    private static String merge(String incoming, String currentValue) {
        return incoming != null ? incoming : currentValue;
    }

    private void ensureNoDuplicates(
        UUID id,
        UserDsResponseModel current,
        String email,
        String login,
        String taxIdentifier
    ) {
        if (
            !email.equals(current.email()) &&
            userDsGateway.existsByEmailExcludingId(email, id)
        ) {
            throw new DuplicateUserException("Email já cadastrado");
        }
        if (
            !login.equals(current.login()) &&
            userDsGateway.existsByLoginExcludingId(login, id)
        ) {
            throw new DuplicateUserException("Login já cadastrado");
        }
        if (
            !taxIdentifier.equals(current.taxIdentifier()) &&
            userDsGateway.existsByTaxIdentifierExcludingId(taxIdentifier, id)
        ) {
            throw new DuplicateUserException("CPF já cadastrado");
        }
    }
}
