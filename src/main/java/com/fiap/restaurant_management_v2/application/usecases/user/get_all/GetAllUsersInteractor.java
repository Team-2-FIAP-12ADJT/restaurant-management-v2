package com.fiap.restaurant_management_v2.application.usecases.user.get_all;

import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.search.FilterCriteria;
import com.fiap.restaurant_management_v2.application.gateways.search.FilterOperator;
import com.fiap.restaurant_management_v2.application.gateways.search.SearchQuery;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GetAllUsersInteractor implements GetAllUsersInputBoundary {

    private final GetAllUsersOutputBoundary outputBoundary;
    private final UserDsGateway userDsGateway;
    private final RestaurantDsGateway restaurantDsGateway;

    public GetAllUsersInteractor(
        GetAllUsersOutputBoundary outputBoundary,
        UserDsGateway userDsGateway,
        RestaurantDsGateway restaurantDsGateway
    ) {
        this.outputBoundary = outputBoundary;
        this.userDsGateway = userDsGateway;
        this.restaurantDsGateway = restaurantDsGateway;
    }

    @Override
    public void execute(GetAllUsersRequestModel request) {
        List<FilterCriteria> criteria = new ArrayList<>();
        addLike(criteria, "name", request.name());
        addLike(criteria, "email", request.email());
        addLike(criteria, "login", request.login());
        addTaxFilter(criteria, request.taxIdentifier());

        PageResult<UserDsResponseModel> page =
            userDsGateway.findAll(
                new SearchQuery(criteria),
                request.page(),
                request.size()
            );

        var ownerIds = page.content().stream().map(UserDsResponseModel::id).toList();

        var restaurantsByOwner = restaurantDsGateway.findAllByOwnerIds(ownerIds).stream()
            .collect(Collectors.groupingBy(RestaurantDsResponseModel::ownerId));

        PageResult<UserSummary> summaryPage = new PageResult<>(
            page.content().stream()
                .map(user -> toSummary(user, restaurantsByOwner.getOrDefault(user.id(), List.of())))
                .toList(),
            page.totalElements(),
            page.page(),
            page.size()
        );

        outputBoundary.present(new GetAllUsersResponseModel(summaryPage));
    }

    private UserSummary toSummary(UserDsResponseModel user, List<RestaurantDsResponseModel> restaurants) {
        return new UserSummary(
            user.id(),
            user.name(),
            user.email(),
            user.login(),
            user.taxIdentifier(),
            user.userTypeName(),
            restaurants
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

    // Sentinela impossível: CPF é armazenado como 11 dígitos crus, então um needle
    // com letras/hífen e SEM wildcard LIKE (%, _) nunca casa. Evita devolver a lista
    // inteira quando o filtro vem sem dígito (ex. "abc", "%", "_").
    private static final String IMPOSSIBLE_TAX_MATCH = "NON-CPF";

    // CPF é armazenado cru (11 dígitos). Normaliza o filtro (tira máscara) para
    // casar. Se o cliente informou algo não-blank mas SEM dígito, troca por uma
    // sentinela impossível: nenhum CPF casa => lista vazia, em vez de descartar o
    // filtro silenciosamente (devolveria tudo) ou injetar wildcard cru (casaria tudo).
    private static void addTaxFilter(
        List<FilterCriteria> criteria,
        String value
    ) {
        if (value == null || value.isBlank()) {
            return;
        }
        String digits = value.replaceAll("\\D", "");
        criteria.add(
            new FilterCriteria(
                "taxIdentifier",
                FilterOperator.LIKE,
                digits.isEmpty() ? IMPOSSIBLE_TAX_MATCH : digits
            )
        );
    }
}
