package com.fiap.restaurant_management_v2.application.usecases.restaurant.get_all;

import com.fiap.restaurant_management_v2.application.exception.UserNotFoundException;
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
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GetAllRestaurantsInteractor
    implements GetAllRestaurantsInputBoundary
{

    private final GetAllRestaurantsOutputBoundary outputBoundary;
    private final RestaurantDsGateway restaurantDsGateway;
    private final UserDsGateway userDsGateway;

    public GetAllRestaurantsInteractor(
        GetAllRestaurantsOutputBoundary outputBoundary,
        RestaurantDsGateway restaurantDsGateway,
        UserDsGateway userDsGateway
    ) {
        this.outputBoundary = outputBoundary;
        this.restaurantDsGateway = restaurantDsGateway;
        this.userDsGateway = userDsGateway;
    }

    @Override
    public void execute(GetAllRestaurantsRequestModel request) {
        List<FilterCriteria> criteria = new ArrayList<>();
        addLike(criteria, "name", request.name());
        addLike(criteria, "cuisineType", request.cuisineType());
        addCnpjFilter(criteria, request.taxIdentifier());

        PageResult<RestaurantDsResponseModel> page =
            restaurantDsGateway.findAll(
                new SearchQuery(criteria),
                request.page(),
                request.size()
            );

        // Batch: 1 query p/ todos os donos da página (dedup via Set) — evita N+1.
        Set<UUID> ownerIds = page
            .content()
            .stream()
            .map(RestaurantDsResponseModel::ownerId)
            .collect(Collectors.toSet());
        Map<UUID, UserDsResponseModel> owners = userDsGateway
            .findAllByIds(ownerIds)
            .stream()
            .collect(
                Collectors.toMap(UserDsResponseModel::id, Function.identity())
            );

        PageResult<RestaurantSummary> summaryPage = new PageResult<>(
            page.content().stream().map(r -> toSummary(r, owners)).toList(),
            page.totalElements(),
            page.page(),
            page.size()
        );

        outputBoundary.present(new GetAllRestaurantsResponseModel(summaryPage));
    }

    private RestaurantSummary toSummary(
        RestaurantDsResponseModel r,
        Map<UUID, UserDsResponseModel> owners
    ) {
        UserDsResponseModel owner = owners.get(r.ownerId());
        if (owner == null) {
            throw new UserNotFoundException(
                "Owner not found with id: " + r.ownerId()
            );
        }
        return new RestaurantSummary(
            r.id(),
            r.name(),
            r.address(),
            r.taxIdentifier(),
            r.cuisineType(),
            r.openingHours(),
            owner
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

    // Sentinela impossível: CNPJ é armazenado como 14 chars [A-Z0-9] crus. Tira
    // tudo que não for alfanumérico (máscara ./-, e wildcards SQL %/_) p/ casar o
    // valor cru; se sobrar vazio (ex. "%"), usa sentinela → não devolve a lista
    // inteira nem injeta wildcard.
    private static final String IMPOSSIBLE_CNPJ_MATCH = "NON-CNPJ";

    private static void addCnpjFilter(
        List<FilterCriteria> criteria,
        String value
    ) {
        if (value == null || value.isBlank()) {
            return;
        }
        String normalized = value
            .replaceAll("[^A-Za-z0-9]", "")
            .toUpperCase();
        criteria.add(
            new FilterCriteria(
                "taxIdentifier",
                FilterOperator.LIKE,
                normalized.isEmpty() ? IMPOSSIBLE_CNPJ_MATCH : normalized
            )
        );
    }
}
