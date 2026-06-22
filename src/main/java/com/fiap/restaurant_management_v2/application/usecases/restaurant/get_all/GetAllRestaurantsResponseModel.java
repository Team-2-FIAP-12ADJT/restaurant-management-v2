package com.fiap.restaurant_management_v2.application.usecases.restaurant.get_all;

import com.fiap.restaurant_management_v2.application.pagination.PageResult;

public record GetAllRestaurantsResponseModel(PageResult<RestaurantSummary> page) {}
