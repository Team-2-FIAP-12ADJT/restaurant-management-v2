package com.fiap.restaurant_management_v2.infrastructure.web;

import com.fiap.restaurant_management_v2.adapters.controllers.UserController;
import com.fiap.restaurant_management_v2.adapters.presenters.CreateUserPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.GetAllUsersPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.GetUserByIdPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.UpdateUserPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.PageViewModel;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.UserViewModel;
import com.fiap.restaurant_management_v2.infrastructure.web.dto.CreateUserRequest;
import com.fiap.restaurant_management_v2.infrastructure.web.dto.GetAllUsersParams;
import com.fiap.restaurant_management_v2.infrastructure.web.dto.UpdateUserRequest;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final GetUserByIdPresenter getUserByIdPresenter;
    private final UpdateUserPresenter updateUserPresenter;

    public UserApi(
        UserController userController,
        CreateUserPresenter createUserPresenter,
        GetAllUsersPresenter getAllUsersPresenter,
        GetUserByIdPresenter getUserByIdPresenter,
        UpdateUserPresenter updateUserPresenter
    ) {
        this.userController = userController;
        this.createUserPresenter = createUserPresenter;
        this.getAllUsersPresenter = getAllUsersPresenter;
        this.getUserByIdPresenter = getUserByIdPresenter;
        this.updateUserPresenter = updateUserPresenter;
    }

    @GetMapping
    @PreAuthorize("hasRole('DONO')")
    public ResponseEntity<PageViewModel<UserViewModel>> getAll(
        @ParameterObject GetAllUsersParams params
    ) {
        userController.getAll(
            params.name(),
            params.email(),
            params.login(),
            params.taxIdentifier(),
            params.page(),
            params.size()
        );

        return ResponseEntity.ok(getAllUsersPresenter.getViewModel());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DONO')")
    public ResponseEntity<UserViewModel> getById(@PathVariable String id) {
        userController.getById(java.util.UUID.fromString(id));
        return ResponseEntity.ok(getUserByIdPresenter.getViewModel());
    }

    @PostMapping
    @PreAuthorize("hasRole('DONO')")
    public ResponseEntity<UserViewModel> create(
        @Valid @RequestBody CreateUserRequest request
    ) {
        userController.create(
            request.name(),
            request.email(),
            request.login(),
            request.taxIdentifier(),
            request.password()
        );

        UserViewModel viewModel = createUserPresenter.getViewModel();

        return ResponseEntity.status(HttpStatus.CREATED).body(viewModel);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DONO')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userController.delete(java.util.UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('DONO')")
    public ResponseEntity<UserViewModel> update(
        @PathVariable String id,
        @Valid @RequestBody UpdateUserRequest request
    ) {
        userController.update(
            java.util.UUID.fromString(id),
            request.name(),
            request.email(),
            request.login(),
            request.taxIdentifier()
        );

        return ResponseEntity.ok(updateUserPresenter.getViewModel());
    }
}
