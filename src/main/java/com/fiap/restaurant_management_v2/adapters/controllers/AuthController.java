package com.fiap.restaurant_management_v2.adapters.controllers;

import com.fiap.restaurant_management_v2.application.usecases.auth.login.LoginInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.auth.login.LoginRequestModel;

public class AuthController {

    private final LoginInputBoundary loginInputBoundary;

    public AuthController(LoginInputBoundary loginInputBoundary) {
        this.loginInputBoundary = loginInputBoundary;
    }

    public void login(String login, String password) {
        loginInputBoundary.execute(new LoginRequestModel(login, password));
    }
}
