package com.fiap.restaurant_management_v2.application.usecases.restaurant.update;

import com.fiap.restaurant_management_v2.application.exception.DuplicateRestaurantException;
import com.fiap.restaurant_management_v2.application.exception.RestaurantNotFoundException;
import com.fiap.restaurant_management_v2.application.exception.UserNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.TransactionalExecutor;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;
import com.fiap.restaurant_management_v2.domain.Restaurant;
import java.util.UUID;

public class UpdateRestaurantInteractor
    implements UpdateRestaurantInputBoundary
{

    private final TransactionalExecutor transactionalExecutor;
    private final RestaurantDsGateway restaurantDsGateway;
    private final UserDsGateway userDsGateway;
    private final UpdateRestaurantOutputBoundary outputBoundary;
    private final LoggerGateway loggerGateway;

    public UpdateRestaurantInteractor(
        TransactionalExecutor transactionalExecutor,
        RestaurantDsGateway restaurantDsGateway,
        UserDsGateway userDsGateway,
        UpdateRestaurantOutputBoundary outputBoundary,
        LoggerGateway loggerGateway
    ) {
        this.transactionalExecutor = transactionalExecutor;
        this.restaurantDsGateway = restaurantDsGateway;
        this.userDsGateway = userDsGateway;
        this.outputBoundary = outputBoundary;
        this.loggerGateway = loggerGateway;
    }

    @Override
    public void execute(UpdateRestaurantRequestModel request) {
        transactionalExecutor.execute(() -> {
            RestaurantDsResponseModel current = restaurantDsGateway
                .findById(request.id())
                .orElseThrow(() ->
                    new RestaurantNotFoundException(
                        "Restaurant not found with id: " + request.id()
                    )
                );

            String name =
                request.name() != null ? request.name() : current.name();
            String address =
                request.address() != null
                    ? request.address()
                    : current.address();
            String taxIdentifier =
                request.taxIdentifier() != null
                    ? request.taxIdentifier()
                    : current.taxIdentifier();
            String cuisineType =
                request.cuisineType() != null
                    ? request.cuisineType()
                    : current.cuisineType();
            String openingHours =
                request.openingHours() != null
                    ? request.openingHours()
                    : current.openingHours();
            UUID ownerId =
                request.ownerId() != null
                    ? request.ownerId()
                    : current.ownerId();

            // Busca o owner efetivo: valida existência (404 se não existir) e
            // fornece o objeto completo para a resposta — inclusive quando o
            // owner não muda no PATCH.
            UserDsResponseModel owner = userDsGateway
                .findById(ownerId)
                .orElseThrow(() ->
                    new UserNotFoundException(
                        "Owner not found with id: " + ownerId
                    )
                );

            if (
                !taxIdentifier.equals(current.taxIdentifier()) &&
                restaurantDsGateway.existsByCnpjExcludingId(
                    taxIdentifier,
                    request.id()
                )
            ) {
                throw new DuplicateRestaurantException("CNPJ já cadastrado");
            }

            // Invariante de domínio sobre o estado MESCLADO (não o request cru):
            // campo omitido = current (mantém); presente-blank/ inválido → 400.
            Restaurant.validateDetails(
                name,
                address,
                taxIdentifier,
                cuisineType,
                openingHours
            );

            RestaurantDsResponseModel saved = restaurantDsGateway.update(
                request.id(),
                name,
                address,
                taxIdentifier,
                cuisineType,
                openingHours,
                ownerId
            );

            loggerGateway.info("restaurant updated id={}", saved.id());

            outputBoundary.present(
                new UpdateRestaurantResponseModel(
                    saved.id(),
                    saved.name(),
                    saved.address(),
                    saved.taxIdentifier(),
                    saved.cuisineType(),
                    saved.openingHours(),
                    owner
                )
            );
        });
    }
}
