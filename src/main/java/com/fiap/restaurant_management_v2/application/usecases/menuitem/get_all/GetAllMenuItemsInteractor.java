package com.fiap.restaurant_management_v2.application.usecases.menuitem.get_all;

import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.search.FilterCriteria;
import com.fiap.restaurant_management_v2.application.gateways.search.FilterOperator;
import com.fiap.restaurant_management_v2.application.gateways.search.SearchQuery;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import java.util.ArrayList;
import java.util.List;

public class GetAllMenuItemsInteractor implements GetAllMenuItemsInputBoundary {

    private final GetAllMenuItemsOutputBoundary outputBoundary;
    private final MenuItemDsGateway menuItemDsGateway;

    public GetAllMenuItemsInteractor(
        GetAllMenuItemsOutputBoundary outputBoundary,
        MenuItemDsGateway menuItemDsGateway
    ) {
        this.outputBoundary = outputBoundary;
        this.menuItemDsGateway = menuItemDsGateway;
    }

    @Override
    public void execute(GetAllMenuItemsRequestModel request) {
        List<FilterCriteria> criteria = new ArrayList<>();
        addLike(criteria, "name", request.name());

        PageResult<MenuItemDsResponseModel> page = menuItemDsGateway.findAll(
            new SearchQuery(criteria),
            request.page(),
            request.size()
        );

        PageResult<MenuItemSummary> summaryPage = new PageResult<>(
            page.content().stream().map(this::toSummary).toList(),
            page.totalElements(),
            page.page(),
            page.size()
        );

        outputBoundary.present(new GetAllMenuItemsResponseModel(summaryPage));
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

    private static void addLike(
        List<FilterCriteria> criteria,
        String field,
        String value
    ) {
        if (value != null && !value.isBlank()) {
            criteria.add(new FilterCriteria(field, FilterOperator.LIKE, value));
        }
    }
}
