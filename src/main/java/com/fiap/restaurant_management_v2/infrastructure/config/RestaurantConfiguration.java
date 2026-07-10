package com.fiap.restaurant_management_v2.infrastructure.config;

import com.fiap.restaurant_management_v2.adapters.controllers.RestaurantController;
import com.fiap.restaurant_management_v2.adapters.presenters.CreateRestaurantPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.DeleteRestaurantPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.GetAllRestaurantsPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.GetRestaurantByIdPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.UpdateRestaurantPresenter;
import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.TransactionalExecutor;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.create.CreateRestaurantInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.create.CreateRestaurantInteractor;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.delete.DeleteRestaurantInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.delete.DeleteRestaurantInteractor;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.get_all.GetAllRestaurantsInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.get_all.GetAllRestaurantsInteractor;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.get_by_id.GetRestaurantByIdInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.get_by_id.GetRestaurantByIdInteractor;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.update.UpdateRestaurantInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.update.UpdateRestaurantInteractor;
import com.fiap.restaurant_management_v2.infrastructure.persistence.RestaurantDsGatewayImpl;
import com.fiap.restaurant_management_v2.infrastructure.persistence.RestaurantJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class RestaurantConfiguration {

    @Bean
    public RestaurantDsGateway restaurantDsGateway(
        RestaurantJpaRepository jpaRepository
    ) {
        return new RestaurantDsGatewayImpl(jpaRepository);
    }

    @Bean
    @RequestScope
    public CreateRestaurantPresenter createRestaurantPresenter() {
        return new CreateRestaurantPresenter();
    }

    @Bean
    public CreateRestaurantInputBoundary createRestaurantInputBoundary(
        TransactionalExecutor transactionalExecutor,
        RestaurantDsGateway restaurantDsGateway,
        UserDsGateway userDsGateway,
        CreateRestaurantPresenter createRestaurantPresenter,
        LoggerGateway loggerGateway
    ) {
        return new CreateRestaurantInteractor(
            transactionalExecutor,
            restaurantDsGateway,
            userDsGateway,
            createRestaurantPresenter,
            loggerGateway
        );
    }

    @Bean
    @RequestScope
    public GetAllRestaurantsPresenter getAllRestaurantsPresenter() {
        return new GetAllRestaurantsPresenter();
    }

    @Bean
    public GetAllRestaurantsInputBoundary getAllRestaurantsInputBoundary(
        RestaurantDsGateway restaurantDsGateway,
        UserDsGateway userDsGateway,
        GetAllRestaurantsPresenter getAllRestaurantsPresenter
    ) {
        return new GetAllRestaurantsInteractor(
            getAllRestaurantsPresenter,
            restaurantDsGateway,
            userDsGateway
        );
    }

    @Bean
    @RequestScope
    public GetRestaurantByIdPresenter getRestaurantByIdPresenter() {
        return new GetRestaurantByIdPresenter();
    }

    @Bean
    public GetRestaurantByIdInputBoundary getRestaurantByIdInputBoundary(
        RestaurantDsGateway restaurantDsGateway,
        UserDsGateway userDsGateway,
        GetRestaurantByIdPresenter getRestaurantByIdPresenter
    ) {
        return new GetRestaurantByIdInteractor(
            restaurantDsGateway,
            userDsGateway,
            getRestaurantByIdPresenter
        );
    }

    @Bean
    @RequestScope
    public UpdateRestaurantPresenter updateRestaurantPresenter() {
        return new UpdateRestaurantPresenter();
    }

    @Bean
    public UpdateRestaurantInputBoundary updateRestaurantInputBoundary(
        TransactionalExecutor transactionalExecutor,
        RestaurantDsGateway restaurantDsGateway,
        UserDsGateway userDsGateway,
        UpdateRestaurantPresenter updateRestaurantPresenter,
        LoggerGateway loggerGateway
    ) {
        return new UpdateRestaurantInteractor(
            transactionalExecutor,
            restaurantDsGateway,
            userDsGateway,
            updateRestaurantPresenter,
            loggerGateway
        );
    }

    @Bean
    @RequestScope
    public DeleteRestaurantPresenter deleteRestaurantPresenter() {
        return new DeleteRestaurantPresenter();
    }

    @Bean
    public DeleteRestaurantInputBoundary deleteRestaurantInputBoundary(
        RestaurantDsGateway restaurantDsGateway,
        DeleteRestaurantPresenter deleteRestaurantPresenter,
        LoggerGateway loggerGateway
    ) {
        return new DeleteRestaurantInteractor(
            restaurantDsGateway,
            deleteRestaurantPresenter,
            loggerGateway
        );
    }

    @Bean
    public RestaurantController restaurantController(
        CreateRestaurantInputBoundary createRestaurantInputBoundary,
        GetAllRestaurantsInputBoundary getAllRestaurantsInputBoundary,
        GetRestaurantByIdInputBoundary getRestaurantByIdInputBoundary,
        UpdateRestaurantInputBoundary updateRestaurantInputBoundary,
        DeleteRestaurantInputBoundary deleteRestaurantInputBoundary
    ) {
        return new RestaurantController(
            createRestaurantInputBoundary,
            getAllRestaurantsInputBoundary,
            getRestaurantByIdInputBoundary,
            updateRestaurantInputBoundary,
            deleteRestaurantInputBoundary
        );
    }
}
