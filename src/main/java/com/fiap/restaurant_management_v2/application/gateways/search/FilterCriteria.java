package com.fiap.restaurant_management_v2.application.gateways.search;

public record FilterCriteria(
    String field,
    FilterOperator operator,
    String value
) {}
