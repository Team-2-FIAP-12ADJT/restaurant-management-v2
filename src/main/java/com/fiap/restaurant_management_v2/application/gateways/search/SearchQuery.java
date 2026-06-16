package com.fiap.restaurant_management_v2.application.gateways.search;

import java.util.List;

public record SearchQuery(List<FilterCriteria> criteria) {
    public SearchQuery {
        criteria = criteria == null ? List.of() : List.copyOf(criteria);
    }

    public static SearchQuery empty() {
        return new SearchQuery(List.of());
    }
}
