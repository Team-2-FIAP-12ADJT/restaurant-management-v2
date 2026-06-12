package com.fiap.restaurant_management_v2.infrastructure.web;

import com.fiap.restaurant_management_v2.adapters.controllers.UserController;
import com.fiap.restaurant_management_v2.adapters.presenters.CreateUserPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.UserViewModel;
import com.fiap.restaurant_management_v2.infrastructure.web.dto.CreateUserRequest;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping(ApiPaths.USERS)
public class UserApi {
    private final UserController userController;
    private final CreateUserPresenter createUserPresenter;

    public UserApi(
        UserController userController,
        CreateUserPresenter createUserPresenter
    ) {
        this.userController = userController;
        this.createUserPresenter = createUserPresenter;
    }

    @PostMapping
    public ResponseEntity<UserViewModel> create(
        @Valid @RequestBody CreateUserRequest request,
        UriComponentsBuilder uriBuilder
    ) {
        userController.create(
            request.name(),
            request.email(),
            request.login(),
            request.password()
        );

        UserViewModel viewModel = createUserPresenter.getViewModel();
        URI location = uriBuilder
            .path(ApiPaths.USERS + "/{id}")
            .buildAndExpand(viewModel.id())
            .toUri();

        return ResponseEntity.created(location).body(viewModel);
    }
}
