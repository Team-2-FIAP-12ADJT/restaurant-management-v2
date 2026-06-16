package com.fiap.restaurant_management_v2.infrastructure.web;

import com.fiap.restaurant_management_v2.adapters.controllers.UserController;
import com.fiap.restaurant_management_v2.adapters.presenters.CreateUserPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.GetAllUsersPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.PageViewModel;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.UserViewModel;
import com.fiap.restaurant_management_v2.infrastructure.web.dto.CreateUserRequest;
import com.fiap.restaurant_management_v2.infrastructure.web.dto.GetAllUsersParams;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.USERS)
public class UserApi {

    private final UserController userController;
    private final CreateUserPresenter createUserPresenter;
    private final GetAllUsersPresenter getAllUsersPresenter;

    public UserApi(
        UserController userController,
        CreateUserPresenter createUserPresenter,
        GetAllUsersPresenter getAllUsersPresenter
    ) {
        this.userController = userController;
        this.createUserPresenter = createUserPresenter;
        this.getAllUsersPresenter = getAllUsersPresenter;
    }

    @GetMapping
    public ResponseEntity<PageViewModel<UserViewModel>> getAll(
        @ParameterObject GetAllUsersParams params
    ) {
        userController.getAll(
            params.name(),
            params.email(),
            params.login(),
            params.page(),
            params.size()
        );

        return ResponseEntity.ok(getAllUsersPresenter.getViewModel());
    }

    @PostMapping
    public ResponseEntity<UserViewModel> create(
        @Valid @RequestBody CreateUserRequest request
    ) {
        userController.create(
            request.name(),
            request.email(),
            request.login(),
            request.password()
        );

        UserViewModel viewModel = createUserPresenter.getViewModel();

        return ResponseEntity.status(HttpStatus.CREATED).body(viewModel);
    }
}
