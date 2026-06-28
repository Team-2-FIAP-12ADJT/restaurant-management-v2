package com.fiap.restaurant_management_v2.adapters.controllers;

import com.fiap.restaurant_management_v2.application.usecases.usertype.bind_user.BindUserTypeToUserInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.usertype.bind_user.BindUserTypeToUserRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.usertype.create.CreateUserTypeInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.usertype.create.CreateUserTypeRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.usertype.delete.DeleteUserTypeByIdInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.usertype.delete.DeleteUserTypeByIdRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.usertype.get_all.GetAllUsersTypeInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.usertype.get_all.GetAllUsersTypeRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.usertype.get_type_by_id.GetUserTypeByIdInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.usertype.get_type_by_id.GetUserTypeByIdRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.usertype.update.UpdateUserTypeInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.usertype.update.UpdateUserTypeRequestModel;

import java.util.UUID;

public class UserTypeController {

    private final CreateUserTypeInputBoundary createUserType ;
    private final GetAllUsersTypeInputBoundary getAllUsersType;
    private final GetUserTypeByIdInputBoundary getTypeById ;
    private final UpdateUserTypeInputBoundary updateUserTypeInputBoundary ;
    private final BindUserTypeToUserInputBoundary bindUserTypeToUserInputBoundary;
    private final DeleteUserTypeByIdInputBoundary deleteUserTypeByIdInputBoundary;

    public UserTypeController(CreateUserTypeInputBoundary createUserType,
                              GetAllUsersTypeInputBoundary getAllUsersType,
                              GetUserTypeByIdInputBoundary getTypeById,
                              UpdateUserTypeInputBoundary updateUserTypeInputBoundary,
                              BindUserTypeToUserInputBoundary bindUserTypeToUserInputBoundary,
                              DeleteUserTypeByIdInputBoundary deleteUserTypeByIdInputBoundary
    ) {
        this.createUserType = createUserType;
        this.getAllUsersType = getAllUsersType;
        this.getTypeById = getTypeById;
        this.updateUserTypeInputBoundary = updateUserTypeInputBoundary;
        this.bindUserTypeToUserInputBoundary = bindUserTypeToUserInputBoundary;
        this.deleteUserTypeByIdInputBoundary = deleteUserTypeByIdInputBoundary;
    }

    public void create(
            String userType
    ) {
        createUserType.execute(
                new CreateUserTypeRequestModel(userType)
        );
    }

    public void getAll(
            int page,
            int size
    ) {
        getAllUsersType.execute(
                new GetAllUsersTypeRequestModel(page,size)
        );
    }

    public void getById(UUID id) {
        getTypeById.execute(new GetUserTypeByIdRequestModel(id));
    }

    public void update(
            UUID id,
            String userType
    ) {
        updateUserTypeInputBoundary.execute(
                new UpdateUserTypeRequestModel(id ,userType)
        );
    }

    public void bind(
            UUID userId,
            UUID typeId
    ) {

        bindUserTypeToUserInputBoundary.execute(
                new BindUserTypeToUserRequestModel(userId, typeId)
        );
    }

    public void delete(UUID id) {
        deleteUserTypeByIdInputBoundary.execute(
                new DeleteUserTypeByIdRequestModel(id)
        );
    }
}
