package com.fiap.restaurant_management_v2.infrastructure.web;

import com.fiap.restaurant_management_v2.adapters.controllers.AuthController;
import com.fiap.restaurant_management_v2.adapters.presenters.AuthPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.TokenViewModel;
import com.fiap.restaurant_management_v2.infrastructure.web.dto.LoginRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.AUTH)
public class AuthApi {

    private final AuthController authController;
    private final AuthPresenter authPresenter;

    public AuthApi(AuthController authController, AuthPresenter authPresenter) {
        this.authController = authController;
        this.authPresenter = authPresenter;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenViewModel> login(@Valid @RequestBody LoginRequest request) {
        authController.login(request.login(), request.password());
        return ResponseEntity.ok(authPresenter.getViewModel());
    }
}
