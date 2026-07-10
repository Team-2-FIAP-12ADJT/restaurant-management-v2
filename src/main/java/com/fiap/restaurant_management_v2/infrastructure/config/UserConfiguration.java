package com.fiap.restaurant_management_v2.infrastructure.config;

import com.fiap.restaurant_management_v2.adapters.controllers.UserController;
import com.fiap.restaurant_management_v2.adapters.presenters.CreateUserPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.DeleteUserByIdPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.GetAllUsersPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.GetUserByIdPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.UpdateUserPresenter;
import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import com.fiap.restaurant_management_v2.application.gateways.PasswordEncoderGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.TransactionalExecutor;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.usecases.user.create.CreateUserInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.user.create.CreateUserInteractor;
import com.fiap.restaurant_management_v2.application.usecases.user.delete.DeleteUserByIdInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.user.delete.DeleteUserByIdInteractor;
import com.fiap.restaurant_management_v2.application.usecases.user.get_all.GetAllUsersInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.user.get_all.GetAllUsersInteractor;
import com.fiap.restaurant_management_v2.application.usecases.user.get_user_by_id.GetUserByIdInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.user.get_user_by_id.GetUserByIdInteractor;
import com.fiap.restaurant_management_v2.application.usecases.user.update.UpdateUserInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.user.update.UpdateUserInteractor;
import com.fiap.restaurant_management_v2.infrastructure.persistence.UserDsGatewayImpl;
import com.fiap.restaurant_management_v2.infrastructure.persistence.UserJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class UserConfiguration {

    @Bean
    public UserDsGateway userDsGateway(UserJpaRepository jpaRepository) {
        return new UserDsGatewayImpl(jpaRepository);
    }

    @Bean
    @RequestScope
    public CreateUserPresenter createUserPresenter() {
        return new CreateUserPresenter();
    }

    @Bean
    public CreateUserInputBoundary createUserInputBoundary(
        UserDsGateway userDsGateway,
        PasswordEncoderGateway passwordEncoderGateway,
        CreateUserPresenter createUserPresenter,
        LoggerGateway loggerGateway
    ) {
        return new CreateUserInteractor(
            userDsGateway,
            passwordEncoderGateway,
            createUserPresenter,
            loggerGateway
        );
    }

    @Bean
    @RequestScope
    public GetAllUsersPresenter getAllUsersPresenter() {
        return new GetAllUsersPresenter();
    }

    @Bean
    public GetAllUsersInputBoundary getAllUsersInputBoundary(
        UserDsGateway userDsGateway,
        GetAllUsersPresenter getAllUsersPresenter
    ) {
        return new GetAllUsersInteractor(getAllUsersPresenter, userDsGateway);
    }

    @Bean
    @RequestScope
    public GetUserByIdPresenter getUserByIdPresenter() {
        return new GetUserByIdPresenter();
    }

    @Bean
    public GetUserByIdInputBoundary getUserByIdInputBoundary(
        UserDsGateway userDsGateway,
        GetUserByIdPresenter getUserByIdPresenter
    ) {
        return new GetUserByIdInteractor(userDsGateway, getUserByIdPresenter);
    }

    @Bean
    @RequestScope
    public DeleteUserByIdPresenter deleteUserByIdPresenter() {
        return new DeleteUserByIdPresenter();
    }

    @Bean
    public DeleteUserByIdInputBoundary deleteUserByIdInputBoundary(
        UserDsGateway userDsGateway,
        RestaurantDsGateway restaurantDsGateway,
        DeleteUserByIdPresenter deleteUserByIdPresenter,
        LoggerGateway loggerGateway
    ) {
        return new DeleteUserByIdInteractor(
            userDsGateway,
            restaurantDsGateway,
            deleteUserByIdPresenter,
            loggerGateway
        );
    }

    @Bean
    @RequestScope
    public UpdateUserPresenter updateUserPresenter() {
        return new UpdateUserPresenter();
    }

    @Bean
    public UpdateUserInputBoundary updateUserInputBoundary(
        UserDsGateway userDsGateway,
        TransactionalExecutor transactionalExecutor,
        UpdateUserPresenter updateUserPresenter,
        LoggerGateway loggerGateway
    ) {
        return new UpdateUserInteractor(
            userDsGateway,
            transactionalExecutor,
            updateUserPresenter,
            loggerGateway
        );
    }

    @Bean
    public UserController userController(
        CreateUserInputBoundary createUserInputBoundary,
        GetAllUsersInputBoundary getAllUsersInputBoundary,
        GetUserByIdInputBoundary getUserByIdInputBoundary,
        DeleteUserByIdInputBoundary deleteUserByIdInputBoundary,
        UpdateUserInputBoundary updateUserInputBoundary
    ) {
        return new UserController(
            createUserInputBoundary,
            getAllUsersInputBoundary,
            getUserByIdInputBoundary,
            deleteUserByIdInputBoundary,
            updateUserInputBoundary
        );
    }
}
