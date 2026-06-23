package com.fiap.restaurant_management_v2.adapters.controllers;

import com.fiap.restaurant_management_v2.application.usecases.menuitem.create.CreateMenuItemInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.create.CreateMenuItemRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.delete.DeleteMenuItemInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.delete.DeleteMenuItemRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_all.GetAllMenuItemsInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_all.GetAllMenuItemsRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_by_id.GetMenuItemByIdInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_by_id.GetMenuItemByIdRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_by_restaurant.GetMenuItemsByRestaurantInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_by_restaurant.GetMenuItemsByRestaurantRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.update.UpdateMenuItemInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.update.UpdateMenuItemRequestModel;
import java.math.BigDecimal;
import java.util.UUID;

public class MenuItemController {

    private final CreateMenuItemInputBoundary createMenuItem;
    private final GetAllMenuItemsInputBoundary getAllMenuItems;
    private final GetMenuItemByIdInputBoundary getMenuItemById;
    private final GetMenuItemsByRestaurantInputBoundary getByRestaurant;
    private final UpdateMenuItemInputBoundary updateMenuItem;
    private final DeleteMenuItemInputBoundary deleteMenuItem;

    public MenuItemController(
        CreateMenuItemInputBoundary createMenuItem,
        GetAllMenuItemsInputBoundary getAllMenuItems,
        GetMenuItemByIdInputBoundary getMenuItemById,
        GetMenuItemsByRestaurantInputBoundary getByRestaurant,
        UpdateMenuItemInputBoundary updateMenuItem,
        DeleteMenuItemInputBoundary deleteMenuItem
    ) {
        this.createMenuItem = createMenuItem;
        this.getAllMenuItems = getAllMenuItems;
        this.getMenuItemById = getMenuItemById;
        this.getByRestaurant = getByRestaurant;
        this.updateMenuItem = updateMenuItem;
        this.deleteMenuItem = deleteMenuItem;
    }

    public void create(
        String name,
        String description,
        BigDecimal price,
        boolean onlyLocal,
        String photoPath,
        UUID restaurantId
    ) {
        createMenuItem.execute(
            new CreateMenuItemRequestModel(
                name,
                description,
                price,
                onlyLocal,
                photoPath,
                restaurantId
            )
        );
    }

    public void getAll(String name, int page, int size) {
        getAllMenuItems.execute(
            new GetAllMenuItemsRequestModel(name, page, size)
        );
    }

    public void getById(UUID id) {
        getMenuItemById.execute(new GetMenuItemByIdRequestModel(id));
    }

    public void getByRestaurant(UUID restaurantId, int page, int size) {
        getByRestaurant.execute(
            new GetMenuItemsByRestaurantRequestModel(
                restaurantId,
                page,
                size
            )
        );
    }

    public void update(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        boolean onlyLocal,
        String photoPath,
        UUID restaurantId
    ) {
        updateMenuItem.execute(
            new UpdateMenuItemRequestModel(
                id,
                name,
                description,
                price,
                onlyLocal,
                photoPath,
                restaurantId
            )
        );
    }

    public void delete(UUID id) {
        deleteMenuItem.execute(new DeleteMenuItemRequestModel(id));
    }
}
