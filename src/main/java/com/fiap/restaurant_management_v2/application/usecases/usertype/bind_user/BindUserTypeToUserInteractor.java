package com.fiap.restaurant_management_v2.application.usecases.usertype.bind_user;

import com.fiap.restaurant_management_v2.application.exception.UserHasActiveRestaurantsException;
import com.fiap.restaurant_management_v2.application.exception.UserNotFoundException;
import com.fiap.restaurant_management_v2.application.exception.UserTypeNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsGateway;

public class BindUserTypeToUserInteractor implements BindUserTypeToUserInputBoundary {

    private final UserTypeDsGateway userTypeDsGateway;
    private final UserDsGateway userDsGateway;
    private final RestaurantDsGateway restaurantDsGateway;
    private final LoggerGateway loggerGateway;

    public BindUserTypeToUserInteractor(
            UserTypeDsGateway userTypeDsGateway,
            UserDsGateway userDsGateway,
            RestaurantDsGateway restaurantDsGateway,
            LoggerGateway loggerGateway
    ) {
        this.userTypeDsGateway = userTypeDsGateway;
        this.userDsGateway = userDsGateway;
        this.restaurantDsGateway = restaurantDsGateway;
        this.loggerGateway = loggerGateway;
    }

    @Override
    public void execute(BindUserTypeToUserRequestModel request) {
        userDsGateway.findAllById(request.userId())
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        var newUserType = userTypeDsGateway.findById(request.typeId())
                .orElseThrow(() -> new UserTypeNotFoundException("Tipo de usuário não encontrado"));

        // Guarda: não permitir rebaixar um owner ativo de "Dono" para outro tipo
        if (restaurantDsGateway.existsByOwnerIdAndIsActive(request.userId()) &&
            !("Dono".equalsIgnoreCase(newUserType.userType()))) {
            loggerGateway.warn(
                    "bind blocked: user is active owner userId={} newType={}",
                    request.userId(),
                    newUserType.userType()
            );
            throw new UserHasActiveRestaurantsException(
                    "Não é possível alterar o tipo: usuário é dono de restaurante ativo"
            );
        }

        userDsGateway.bindUserType(request.userId(), request.typeId());
        loggerGateway.info(
                "user type bound userId={} typeId={}",
                request.userId(),
                request.typeId()
        );
    }
}
