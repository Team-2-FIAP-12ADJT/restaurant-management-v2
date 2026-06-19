package com.fiap.restaurant_management_v2.application.usecases.restaurant.get_all;

import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.search.FilterCriteria;
import com.fiap.restaurant_management_v2.application.gateways.search.FilterOperator;
import com.fiap.restaurant_management_v2.application.gateways.search.SearchQuery;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import java.util.ArrayList;
import java.util.List;

public class GetAllRestaurantsInteractor implements GetAllRestaurantsInputBoundary {

    private final GetAllRestaurantsOutputBoundary outputBoundary;
    private final RestaurantDsGateway restaurantDsGateway;

    public GetAllRestaurantsInteractor(
        GetAllRestaurantsOutputBoundary outputBoundary,
        RestaurantDsGateway restaurantDsGateway
    ) {
        this.outputBoundary = outputBoundary;
        this.restaurantDsGateway = restaurantDsGateway;
    }

    @Override
    public void execute(GetAllRestaurantsRequestModel request) {
        List<FilterCriteria> criteria = new ArrayList<>();
        addLike(criteria, "name", request.name());
        addLike(criteria, "cuisineType", request.cuisineType());

        PageResult<RestaurantDsResponseModel> page = restaurantDsGateway.findAll(
            new SearchQuery(criteria),
            request.page(),
            request.size()
        );

        PageResult<RestaurantSummary> summaryPage = new PageResult<>(
            page.content().stream().map(this::toSummary).toList(),
            page.totalElements(),
            page.page(),
            page.size()
        );

        outputBoundary.present(new GetAllRestaurantsResponseModel(summaryPage));
    }

    private RestaurantSummary toSummary(RestaurantDsResponseModel r) {
        return new RestaurantSummary(
            r.id(), r.name(), r.address(), r.cuisineType(), r.openingHours(), r.ownerId()
        );
    }

    private static void addLike(List<FilterCriteria> criteria, String field, String value) {
        if (value != null && !value.isBlank()) {
            criteria.add(new FilterCriteria(field, FilterOperator.LIKE, value));
        }
    }
}
