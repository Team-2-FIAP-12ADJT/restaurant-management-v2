package com.fiap.restaurant_management_v2.application.usecases.usertype.delete;

import com.fiap.restaurant_management_v2.application.exception.UserTypeInUseByActiveOwnerException;
import com.fiap.restaurant_management_v2.application.exception.UserTypeNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.TransactionalExecutor;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsResponseModel;
import java.util.UUID;

public class DeleteUserTypeByIdInteractor implements DeleteUserTypeByIdInputBoundary {

    private final UserTypeDsGateway userTypeDsGateway;
    private final UserDsGateway userDsGateway;
    private final RestaurantDsGateway restaurantDsGateway;
    private final TransactionalExecutor transactionalExecutor;
    private final DeleteUserTypeByIdOutputBoundary outputBoundary;
    private final LoggerGateway loggerGateway;

    public DeleteUserTypeByIdInteractor(
            UserTypeDsGateway userTypeDsGateway,
            UserDsGateway userDsGateway,
            RestaurantDsGateway restaurantDsGateway,
            TransactionalExecutor transactionalExecutor,
            DeleteUserTypeByIdOutputBoundary outputBoundary,
            LoggerGateway loggerGateway
    ) {
        this.userTypeDsGateway = userTypeDsGateway;
        this.userDsGateway = userDsGateway;
        this.restaurantDsGateway = restaurantDsGateway;
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

        // Guarda: não permitir deletar userType se existir user ativo vinculado
        // que seja dono de restaurante ativo
        var activeUserIds = userDsGateway.findActiveIdsByUserTypeId(userType.id());
        for (UUID userId : activeUserIds) {
            if (restaurantDsGateway.existsByOwnerIdAndIsActive(userId)) {
                loggerGateway.warn(
                        "delete type blocked: active owner using type typeId={} userId={}",
                        userType.id(),
                        userId
                );
                throw new UserTypeInUseByActiveOwnerException(
                        "Não é possível deletar o tipo: há usuário ativo vinculado que é dono de restaurante ativo"
                );
            }
        }

        transactionalExecutor.execute(() -> {
            userDsGateway.unbindUserType(userType.id());
            userTypeDsGateway.deleteById(userType.id());
        });
        loggerGateway.info("user type deleted id={}", userType.id());
        outputBoundary.present();
    }
}
