package com.fiap.restaurant_management_v2.application.usecases.menuitem.get_by_restaurant;

import com.fiap.restaurant_management_v2.application.exception.RestaurantNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_all.MenuItemSummary;

public class GetMenuItemsByRestaurantInteractor
    implements GetMenuItemsByRestaurantInputBoundary {

    private final GetMenuItemsByRestaurantOutputBoundary outputBoundary;
    private final MenuItemDsGateway menuItemDsGateway;
    private final RestaurantDsGateway restaurantDsGateway;

    public GetMenuItemsByRestaurantInteractor(
        GetMenuItemsByRestaurantOutputBoundary outputBoundary,
        MenuItemDsGateway menuItemDsGateway,
        RestaurantDsGateway restaurantDsGateway
    ) {
        this.outputBoundary = outputBoundary;
        this.menuItemDsGateway = menuItemDsGateway;
        this.restaurantDsGateway = restaurantDsGateway;
    }

    @Override
    public void execute(GetMenuItemsByRestaurantRequestModel request) {
        if (!restaurantDsGateway.existsById(request.restaurantId())) {
            throw new RestaurantNotFoundException(
                "Restaurante não encontrado: " + request.restaurantId()
            );
        }

        PageResult<MenuItemDsResponseModel> page =
            menuItemDsGateway.findAllByRestaurant(
                request.restaurantId(),
                request.page(),
                request.size()
            );

        PageResult<MenuItemSummary> summaryPage = new PageResult<>(
            page.content().stream().map(this::toSummary).toList(),
            page.totalElements(),
            page.page(),
            page.size()
        );

        outputBoundary.present(
            new GetMenuItemsByRestaurantResponseModel(summaryPage)
        );
    }

    private MenuItemSummary toSummary(MenuItemDsResponseModel menuItem) {
        return new MenuItemSummary(
            menuItem.id(),
            menuItem.name(),
            menuItem.description(),
            menuItem.price(),
            menuItem.onlyLocal(),
            menuItem.photoPath(),
            menuItem.restaurantId()
        );
    }
}
