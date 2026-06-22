package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.application.usecases.restaurant.delete.DeleteRestaurantOutputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.delete.DeleteRestaurantResponseModel;

public class DeleteRestaurantPresenter implements DeleteRestaurantOutputBoundary {

    private Boolean deleted = false;

    @Override
    public void present(DeleteRestaurantResponseModel response) {
        this.deleted = true;
    }

    public Boolean isDeleted() {
        return deleted;
    }
}
