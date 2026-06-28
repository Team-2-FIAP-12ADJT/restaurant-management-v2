package com.fiap.restaurant_management_v2.infrastructure.config;

import com.fiap.restaurant_management_v2.adapters.controllers.UserTypeController;
import com.fiap.restaurant_management_v2.adapters.presenters.CreateUserTypePresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.DeleteUserTypeByIdPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.GetAllUsersTypePresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.GetUserTypeByIdPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.UpdateUserTypePresenter;
import com.fiap.restaurant_management_v2.application.gateways.TransactionalExecutor;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsGateway;
import com.fiap.restaurant_management_v2.application.usecases.usertype.bind_user.BindUserTypeToUserInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.usertype.bind_user.BindUserTypeToUserInteractor;
import com.fiap.restaurant_management_v2.application.usecases.usertype.create.CreateUserTypeInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.usertype.create.CreateUserTypeInteractor;
import com.fiap.restaurant_management_v2.application.usecases.usertype.create.CreateUserTypeOutputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.usertype.delete.DeleteUserTypeByIdInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.usertype.delete.DeleteUserTypeByIdInteractor;
import com.fiap.restaurant_management_v2.application.usecases.usertype.delete.DeleteUserTypeByIdOutputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.usertype.get_all.GetAllUsersTypeInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.usertype.get_all.GetAllUsersTypeInteractor;
import com.fiap.restaurant_management_v2.application.usecases.usertype.get_type_by_id.GetUserTypeByIdInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.usertype.get_type_by_id.GetUserTypeByIdInteractor;
import com.fiap.restaurant_management_v2.application.usecases.usertype.update.UpdateUserTypeInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.usertype.update.UpdateUserTypeInteractor;
import com.fiap.restaurant_management_v2.infrastructure.persistence.UserTypeDsGatewayImpl;
import com.fiap.restaurant_management_v2.infrastructure.persistence.UserTypeJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class UserTypeConfiguration {

    @Bean
    public UserTypeDsGateway userTypeDsGateway(UserTypeJpaRepository jpaRepository) {
        return new UserTypeDsGatewayImpl(jpaRepository);
    }

    @Bean
    @RequestScope
    public CreateUserTypePresenter createUserTypePresenter() {
        return new CreateUserTypePresenter();
    }



    @Bean
    public CreateUserTypeInputBoundary createUserTypeInputBoundary  (
            UserTypeDsGateway userTypeDsGateway,
            CreateUserTypeOutputBoundary createUserTypePresenter
    ) {
        return new CreateUserTypeInteractor(
                userTypeDsGateway,
                createUserTypePresenter

        );
    }

    @Bean
    @RequestScope
    public GetAllUsersTypePresenter getAllUsersTypePresenter () {
        return new GetAllUsersTypePresenter();
    }

    @Bean
    public GetAllUsersTypeInputBoundary getAllUsersTypeInputBoundary(
            UserTypeDsGateway userTypeDsGateway,
            GetAllUsersTypePresenter getAllUsersTypePresenter
    ) {
        return new GetAllUsersTypeInteractor(getAllUsersTypePresenter, userTypeDsGateway);
    }

    @Bean
    @RequestScope
    public GetUserTypeByIdPresenter getUserTypeByIdPresenter() {
        return new GetUserTypeByIdPresenter();
    }

    @Bean
    public GetUserTypeByIdInputBoundary getUserTypeByIdInputBoundary(
            UserTypeDsGateway userTypeDsGateway,
            GetUserTypeByIdPresenter getUserTypeByIdPresenter
    ) {
        return new GetUserTypeByIdInteractor(userTypeDsGateway, getUserTypeByIdPresenter);
    }

    @Bean
    @RequestScope
    public UpdateUserTypePresenter updateUserTypePresenter() {
        return new UpdateUserTypePresenter();
    }

    @Bean
    public UpdateUserTypeInputBoundary updateUserTypeInputBoundary(
            UserTypeDsGateway userTypeDsGateway,
            UpdateUserTypePresenter updateUserTypePresenter
    ) {
        return new UpdateUserTypeInteractor(userTypeDsGateway, updateUserTypePresenter);
    }

    @Bean
    @RequestScope
    public DeleteUserTypeByIdPresenter deleteUserTypeByIdPresenter() {
        return new DeleteUserTypeByIdPresenter();
    }

    @Bean
    public DeleteUserTypeByIdInputBoundary deleteUserTypeByIdInputBoundary(
            UserTypeDsGateway userTypeDsGateway,
            UserDsGateway userDsGateway,
            TransactionalExecutor transactionalExecutor,
            DeleteUserTypeByIdOutputBoundary deleteUserTypeByIdPresenter
    ) {
        return new DeleteUserTypeByIdInteractor(
                userTypeDsGateway,
                userDsGateway,
                transactionalExecutor,
                deleteUserTypeByIdPresenter
        );
    }

    @Bean
    public BindUserTypeToUserInputBoundary bindUserTypeToUserInputBoundary(
            UserTypeDsGateway userTypeDsGateway,
            UserDsGateway userDsGateway
    ) {
        return new BindUserTypeToUserInteractor(userTypeDsGateway, userDsGateway);
    }


    @Bean
    public UserTypeController userTypeController(
            CreateUserTypeInputBoundary createUserTypeInputBoundary ,
            GetAllUsersTypeInputBoundary getAllUsersInputBoundary,
            GetUserTypeByIdInputBoundary getUserTypeByIdInputBoundary,
            UpdateUserTypeInputBoundary updateUserTypeInputBoundary,
            BindUserTypeToUserInputBoundary bindUserTypeToUserInputBoundary,
            DeleteUserTypeByIdInputBoundary deleteUserTypeByIdInputBoundary
    ) {
        return new UserTypeController(
                createUserTypeInputBoundary,
                getAllUsersInputBoundary,
                getUserTypeByIdInputBoundary,
                updateUserTypeInputBoundary,
                bindUserTypeToUserInputBoundary,
                deleteUserTypeByIdInputBoundary
        );
    }


}
