package com.fiap.restaurant_management_v2.infrastructure.persistence;

import com.fiap.restaurant_management_v2.application.exception.InvalidFilterException;
import com.fiap.restaurant_management_v2.application.gateways.search.FilterCriteria;
import com.fiap.restaurant_management_v2.application.gateways.search.SearchQuery;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.domain.Specification;

final class SpecificationBuilder {

    private SpecificationBuilder() {}

    static <T> Specification<T> build(
        SearchQuery query,
        Map<String, String> allowedFields
    ) {
        List<Specification<T>> specs = query
            .criteria()
            .stream()
            .filter(c -> c.value() != null && !c.value().isBlank())
            .map(c -> SpecificationBuilder.<T>toSpec(c, allowedFields))
            .toList();

        return Specification.allOf(specs); // empty list → matches all
    }

    private static <T> Specification<T> toSpec(
        FilterCriteria criteria,
        Map<String, String> allowedFields
    ) {
        String attribute = allowedFields.get(criteria.field());
        if (attribute == null) {
            throw new InvalidFilterException(criteria.field());
        }

        return (root, query, cb) ->
            switch (criteria.operator()) {
                case LIKE -> cb.like(
                    cb.lower(root.get(attribute)),
                    "%" + criteria.value().toLowerCase() + "%"
                );
                case EQUALS -> cb.equal(root.get(attribute), criteria.value());
            };
    }
}
