package com.fiap.restaurant_management_v2.adapters.controllers;

import com.fiap.restaurant_management_v2.application.usecases.restaurant.create.CreateRestaurantInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.create.CreateRestaurantRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.delete.DeleteRestaurantInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.delete.DeleteRestaurantRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.get_all.GetAllRestaurantsInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.get_all.GetAllRestaurantsRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.get_by_id.GetRestaurantByIdInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.get_by_id.GetRestaurantByIdRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.update.UpdateRestaurantInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.update.UpdateRestaurantRequestModel;
import java.util.UUID;

public class RestaurantController {

    private final CreateRestaurantInputBoundary createRestaurant;
    private final GetAllRestaurantsInputBoundary getAllRestaurants;
    private final GetRestaurantByIdInputBoundary getRestaurantById;
    private final UpdateRestaurantInputBoundary updateRestaurant;
    private final DeleteRestaurantInputBoundary deleteRestaurant;

    public RestaurantController(
        CreateRestaurantInputBoundary createRestaurant,
        GetAllRestaurantsInputBoundary getAllRestaurants,
        GetRestaurantByIdInputBoundary getRestaurantById,
        UpdateRestaurantInputBoundary updateRestaurant,
        DeleteRestaurantInputBoundary deleteRestaurant
    ) {
        this.createRestaurant = createRestaurant;
        this.getAllRestaurants = getAllRestaurants;
        this.getRestaurantById = getRestaurantById;
        this.updateRestaurant = updateRestaurant;
        this.deleteRestaurant = deleteRestaurant;
    }

    public void create(
        String name,
        String address,
        String taxIdentifier,
        String cuisineType,
        String openingHours,
        UUID ownerId
    ) {
        createRestaurant.execute(
            new CreateRestaurantRequestModel(
                name,
                address,
                taxIdentifier,
                cuisineType,
                openingHours,
                ownerId
            )
        );
    }

    public void getAll(
        String name,
        String taxIdentifier,
        String cuisineType,
        int page,
        int size
    ) {
        getAllRestaurants.execute(
            new GetAllRestaurantsRequestModel(
                name,
                taxIdentifier,
                cuisineType,
                page,
                size
            )
        );
    }

    public void getById(UUID id) {
        getRestaurantById.execute(new GetRestaurantByIdRequestModel(id));
    }

    public void update(
        UUID id,
        String name,
        String address,
        String taxIdentifier,
        String cuisineType,
        String openingHours,
        UUID ownerId
    ) {
        updateRestaurant.execute(
            new UpdateRestaurantRequestModel(
                id,
                name,
                address,
                taxIdentifier,
                cuisineType,
                openingHours,
                ownerId
            )
        );
    }

    public void delete(UUID id) {
        deleteRestaurant.execute(new DeleteRestaurantRequestModel(id));
    }
}
