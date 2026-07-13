package com.fiap.restaurant_management_v2.application.usecases.usertype.update;

import com.fiap.restaurant_management_v2.application.exception.DuplicateUserTypeException;
import com.fiap.restaurant_management_v2.application.exception.UserTypeInUseByActiveOwnerException;
import com.fiap.restaurant_management_v2.application.exception.UserTypeNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsResponseModel;
import com.fiap.restaurant_management_v2.domain.UserType;
import java.util.UUID;

public class UpdateUserTypeInteractor implements UpdateUserTypeInputBoundary {

    private final UserTypeDsGateway userTypeDsGateway;
    private final UserDsGateway userDsGateway;
    private final RestaurantDsGateway restaurantDsGateway;
    private final UpdateUserTypeOutputBoundary outputBoundary;
    private final LoggerGateway loggerGateway;

    public UpdateUserTypeInteractor(
        UserTypeDsGateway userTypeDsGateway,
        UserDsGateway userDsGateway,
        RestaurantDsGateway restaurantDsGateway,
        UpdateUserTypeOutputBoundary outputBoundary,
        LoggerGateway loggerGateway
    ) {
        this.userTypeDsGateway = userTypeDsGateway;
        this.userDsGateway = userDsGateway;
        this.restaurantDsGateway = restaurantDsGateway;
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

        // Guarda: não permitir renomear tipo PARA != 'Dono' se tem user ativo que é owner ativo
        if (!request.userType().equalsIgnoreCase("Dono")) {
            var activeUserIds = userDsGateway.findActiveIdsByUserTypeId(request.id());
            for (UUID userId : activeUserIds) {
                if (restaurantDsGateway.existsByOwnerIdAndIsActive(userId)) {
                    loggerGateway.warn(
                            "rename type blocked: active owner using type typeId={} userId={}",
                            request.id(),
                            userId
                    );
                    throw new UserTypeInUseByActiveOwnerException(
                            "Não é possível renomear o tipo: há usuário ativo vinculado que é dono de restaurante ativo"
                    );
                }
            }
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
