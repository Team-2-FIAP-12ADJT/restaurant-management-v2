package com.fiap.restaurant_management_v2.infrastructure.web;

import com.fiap.restaurant_management_v2.adapters.controllers.UserTypeController;
import com.fiap.restaurant_management_v2.adapters.presenters.CreateUserTypePresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.GetAllUsersTypePresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.GetUserTypeByIdPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.UpdateUserTypePresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.PageViewModel;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.UserTypeViewModel;
import com.fiap.restaurant_management_v2.infrastructure.web.dto.BindUserTypeRequest;
import com.fiap.restaurant_management_v2.infrastructure.web.dto.CreateUserTypeRequest;
import com.fiap.restaurant_management_v2.infrastructure.web.dto.GetAllUsersTypeParams;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.USERS_TYPE)
public class TypeUserApi {

    private  final UserTypeController userTypeController ;
    private  final CreateUserTypePresenter createUserTypePresenter ;
    private  final GetAllUsersTypePresenter getAllUsersTypePresenter ;
    private final GetUserTypeByIdPresenter getUserTypeByIdPresenter ;
    private final UpdateUserTypePresenter updateUserTypePresenter;

    public TypeUserApi(UserTypeController userTypeController, CreateUserTypePresenter createUserTypePresenter, GetAllUsersTypePresenter getAllUsersTypePresenter, GetUserTypeByIdPresenter getUserTypeByIdPresenter, UpdateUserTypePresenter updateUserTypePresenter) {
        this.userTypeController = userTypeController;
        this.createUserTypePresenter = createUserTypePresenter;
        this.getAllUsersTypePresenter = getAllUsersTypePresenter;
        this.getUserTypeByIdPresenter = getUserTypeByIdPresenter;
        this.updateUserTypePresenter = updateUserTypePresenter;
    }

    @GetMapping
    public ResponseEntity<PageViewModel<UserTypeViewModel>> getAll(
            @ParameterObject GetAllUsersTypeParams params
    ) {
        userTypeController.getAll(params.page(), params.size());
        return ResponseEntity.ok(getAllUsersTypePresenter.getViewModel());

    }

    @GetMapping("/{id}")
    public ResponseEntity<UserTypeViewModel> getById(@PathVariable String id) {
        userTypeController.getById(java.util.UUID.fromString(id));
        return ResponseEntity.ok(getUserTypeByIdPresenter.getViewModel());

    }


    @PostMapping
    public ResponseEntity<UserTypeViewModel> create(
            @Valid @RequestBody CreateUserTypeRequest request
    ){
        userTypeController.create( request.userType());
        UserTypeViewModel viewModel = createUserTypePresenter.getViewModel();
        return ResponseEntity.status(HttpStatus.CREATED).body(viewModel);

    }

    @PutMapping("/{id}")
    public ResponseEntity<UserTypeViewModel> update(
            @PathVariable UUID id ,
            @Valid @RequestBody CreateUserTypeRequest request
    ){

        userTypeController.update(id ,request.userType());
        UserTypeViewModel viewModel = updateUserTypePresenter.getViewModel();
        return ResponseEntity.status(HttpStatus.OK).body(viewModel);

    }


    @PostMapping("/bind")
    public ResponseEntity<Void> bind(
            @Valid @RequestBody BindUserTypeRequest request
    ){
        userTypeController.bind(request.userId() , request.typeId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        userTypeController.delete(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }
}
