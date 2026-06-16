package com.fiap.restaurant_management_v2.application.usecases.user.get_all;

import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.search.FilterCriteria;
import com.fiap.restaurant_management_v2.application.gateways.search.FilterOperator;
import com.fiap.restaurant_management_v2.application.gateways.search.SearchQuery;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import java.util.ArrayList;
import java.util.List;

public class GetAllUsersInteractor implements GetAllUsersInputBoundary {

    private final GetAllUsersOutputBoundary outputBoundary;
    private final UserDsGateway userDsGateway;

    public GetAllUsersInteractor(
        GetAllUsersOutputBoundary outputBoundary,
        UserDsGateway userDsGateway
    ) {
        this.outputBoundary = outputBoundary;
        this.userDsGateway = userDsGateway;
    }

    @Override
    public void execute(GetAllUsersRequestModel request) {
        List<FilterCriteria> criteria = new ArrayList<>();
        addLike(criteria, "name", request.name());
        addLike(criteria, "email", request.email());
        addLike(criteria, "login", request.login());

        PageResult<UserDsResponseModel> page = userDsGateway.findAll(
            new SearchQuery(criteria),
            request.page(),
            request.size()
        );

        PageResult<UserSummary> summaryPage = new PageResult<>(
            page.content().stream().map(this::toSummary).toList(),
            page.totalElements(),
            page.page(),
            page.size()
        );

        outputBoundary.present(new GetAllUsersResponseModel(summaryPage));
    }

    private UserSummary toSummary(UserDsResponseModel user) {
        return new UserSummary(
            user.id(),
            user.name(),
            user.email(),
            user.login()
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
