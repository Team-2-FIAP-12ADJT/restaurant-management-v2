package com.fiap.restaurant_management_v2.infrastructure.config;

import com.fiap.restaurant_management_v2.adapters.controllers.UserController;
import com.fiap.restaurant_management_v2.adapters.presenters.CreateUserPresenter;
import com.fiap.restaurant_management_v2.application.gateways.PasswordEncoderGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.usecases.user.create.CreateUserInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.user.create.CreateUserInteractor;
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
        CreateUserPresenter createUserPresenter
    ) {
        return new CreateUserInteractor(userDsGateway, passwordEncoderGateway, createUserPresenter);
    }

    @Bean
    public UserController userController(CreateUserInputBoundary createUserInputBoundary) {
        return new UserController(createUserInputBoundary);
    }
}
