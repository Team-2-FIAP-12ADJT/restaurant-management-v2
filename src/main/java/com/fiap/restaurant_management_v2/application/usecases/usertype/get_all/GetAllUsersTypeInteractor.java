package com.fiap.restaurant_management_v2.application.usecases.usertype.get_all;

import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.search.FilterCriteria;
import com.fiap.restaurant_management_v2.application.gateways.search.SearchQuery;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;

import java.util.ArrayList;
import java.util.List;


public class GetAllUsersTypeInteractor implements GetAllUsersTypeInputBoundary {

    private final GetAllUsersTypeOutputBoundary outputBoundary;
    private final UserTypeDsGateway userTypeDsGateway;

    public GetAllUsersTypeInteractor(
            GetAllUsersTypeOutputBoundary outputBoundary,
            UserTypeDsGateway userTypeDsGateway
    ) {
        this.outputBoundary = outputBoundary;
        this.userTypeDsGateway = userTypeDsGateway;

    }

    @Override
    public void execute(GetAllUsersTypeRequestModel request) {
        List<FilterCriteria> criteria = new ArrayList<>();

        PageResult<UserTypeDsResponseModel> page =
                userTypeDsGateway.findAll(
                        new SearchQuery(criteria),
                        request.page(),
                        request.size()
            );

        PageResult<UserTypeSummary> summaryPage = new PageResult<>(
            page.content().stream().map(this::toSummary).toList(),
            page.totalElements(),
            page.page(),
            page.size()
        );

        outputBoundary.present(new GetAllUsersTypeResponseModel(summaryPage));
    }

    private UserTypeSummary toSummary(UserTypeDsResponseModel userType) {
        return new UserTypeSummary(
            userType.id(),
            userType.userType()
        );
    }
}
