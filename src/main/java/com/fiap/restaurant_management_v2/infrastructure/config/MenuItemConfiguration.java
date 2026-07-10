package com.fiap.restaurant_management_v2.infrastructure.config;

import com.fiap.restaurant_management_v2.adapters.controllers.MenuItemController;
import com.fiap.restaurant_management_v2.adapters.presenters.CreateMenuItemPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.DeleteMenuItemPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.GetAllMenuItemsPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.GetMenuItemByIdPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.GetMenuItemsByRestaurantPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.UpdateMenuItemPresenter;
import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.TransactionalExecutor;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.create.CreateMenuItemInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.create.CreateMenuItemInteractor;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.delete.DeleteMenuItemInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.delete.DeleteMenuItemInteractor;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_all.GetAllMenuItemsInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_all.GetAllMenuItemsInteractor;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_by_id.GetMenuItemByIdInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_by_id.GetMenuItemByIdInteractor;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_by_restaurant.GetMenuItemsByRestaurantInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_by_restaurant.GetMenuItemsByRestaurantInteractor;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.update.UpdateMenuItemInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.update.UpdateMenuItemInteractor;
import com.fiap.restaurant_management_v2.infrastructure.persistence.MenuItemDsGatewayImpl;
import com.fiap.restaurant_management_v2.infrastructure.persistence.MenuItemJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class MenuItemConfiguration {

    @Bean
    public MenuItemDsGateway menuItemDsGateway(
        MenuItemJpaRepository jpaRepository
    ) {
        return new MenuItemDsGatewayImpl(jpaRepository);
    }

    @Bean
    @RequestScope
    public CreateMenuItemPresenter createMenuItemPresenter() {
        return new CreateMenuItemPresenter();
    }

    @Bean
    public CreateMenuItemInputBoundary createMenuItemInputBoundary(
        MenuItemDsGateway menuItemDsGateway,
        RestaurantDsGateway restaurantDsGateway,
        CreateMenuItemPresenter presenter,
        LoggerGateway loggerGateway
    ) {
        return new CreateMenuItemInteractor(
            menuItemDsGateway,
            restaurantDsGateway,
            presenter,
            loggerGateway
        );
    }

    @Bean
    @RequestScope
    public GetAllMenuItemsPresenter getAllMenuItemsPresenter() {
        return new GetAllMenuItemsPresenter();
    }

    @Bean
    public GetAllMenuItemsInputBoundary getAllMenuItemsInputBoundary(
        MenuItemDsGateway menuItemDsGateway,
        GetAllMenuItemsPresenter presenter
    ) {
        return new GetAllMenuItemsInteractor(presenter, menuItemDsGateway);
    }

    @Bean
    @RequestScope
    public GetMenuItemByIdPresenter getMenuItemByIdPresenter() {
        return new GetMenuItemByIdPresenter();
    }

    @Bean
    public GetMenuItemByIdInputBoundary getMenuItemByIdInputBoundary(
        MenuItemDsGateway menuItemDsGateway,
        GetMenuItemByIdPresenter presenter
    ) {
        return new GetMenuItemByIdInteractor(menuItemDsGateway, presenter);
    }

    @Bean
    @RequestScope
    public GetMenuItemsByRestaurantPresenter getByRestaurantPresenter() {
        return new GetMenuItemsByRestaurantPresenter();
    }

    @Bean
    public GetMenuItemsByRestaurantInputBoundary getByRestaurantInputBoundary(
        MenuItemDsGateway menuItemDsGateway,
        RestaurantDsGateway restaurantDsGateway,
        GetMenuItemsByRestaurantPresenter presenter
    ) {
        return new GetMenuItemsByRestaurantInteractor(
            presenter,
            menuItemDsGateway,
            restaurantDsGateway
        );
    }

    @Bean
    @RequestScope
    public UpdateMenuItemPresenter updateMenuItemPresenter() {
        return new UpdateMenuItemPresenter();
    }

    @Bean
    public UpdateMenuItemInputBoundary updateMenuItemInputBoundary(
        TransactionalExecutor transactionalExecutor,
        MenuItemDsGateway menuItemDsGateway,
        RestaurantDsGateway restaurantDsGateway,
        UpdateMenuItemPresenter presenter,
        LoggerGateway loggerGateway
    ) {
        return new UpdateMenuItemInteractor(
            transactionalExecutor,
            menuItemDsGateway,
            restaurantDsGateway,
            presenter,
            loggerGateway
        );
    }

    @Bean
    @RequestScope
    public DeleteMenuItemPresenter deleteMenuItemPresenter() {
        return new DeleteMenuItemPresenter();
    }

    @Bean
    public DeleteMenuItemInputBoundary deleteMenuItemInputBoundary(
        MenuItemDsGateway menuItemDsGateway,
        DeleteMenuItemPresenter presenter,
        LoggerGateway loggerGateway
    ) {
        return new DeleteMenuItemInteractor(menuItemDsGateway, presenter, loggerGateway);
    }

    @Bean
    public MenuItemController menuItemController(
        CreateMenuItemInputBoundary createMenuItem,
        GetAllMenuItemsInputBoundary getAllMenuItems,
        GetMenuItemByIdInputBoundary getMenuItemById,
        GetMenuItemsByRestaurantInputBoundary getByRestaurant,
        UpdateMenuItemInputBoundary updateMenuItem,
        DeleteMenuItemInputBoundary deleteMenuItem
    ) {
        return new MenuItemController(
            createMenuItem,
            getAllMenuItems,
            getMenuItemById,
            getByRestaurant,
            updateMenuItem,
            deleteMenuItem
        );
    }
}
