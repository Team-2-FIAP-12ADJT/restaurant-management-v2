package com.fiap.restaurant_management_v2.infrastructure.config;

import com.fiap.restaurant_management_v2.adapters.controllers.AuthController;
import com.fiap.restaurant_management_v2.adapters.presenters.AuthPresenter;
import com.fiap.restaurant_management_v2.application.gateways.PasswordEncoderGateway;
import com.fiap.restaurant_management_v2.application.gateways.TokenGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.usecases.auth.login.LoginInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.auth.login.LoginInteractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class AuthConfiguration {

    @Bean
    @RequestScope
    public AuthPresenter authPresenter() {
        return new AuthPresenter();
    }

    @Bean
    public LoginInputBoundary loginInputBoundary(
        UserDsGateway userDsGateway,
        PasswordEncoderGateway passwordEncoderGateway,
        TokenGateway tokenGateway,
        AuthPresenter authPresenter
    ) {
        return new LoginInteractor(
            userDsGateway,
            passwordEncoderGateway,
            tokenGateway,
            authPresenter
        );
    }

    @Bean
    public AuthController authController(LoginInputBoundary loginInputBoundary) {
        return new AuthController(loginInputBoundary);
    }
}
