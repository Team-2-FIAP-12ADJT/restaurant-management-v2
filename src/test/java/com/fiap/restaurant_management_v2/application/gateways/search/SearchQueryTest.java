package com.fiap.restaurant_management_v2.application.gateways.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SearchQueryTest {

    @Test
    @DisplayName("Construtor copia a lista e torna imutável")
    void copiesListImmutably() {
        List<FilterCriteria> original = new ArrayList<>();
        original.add(new FilterCriteria("f", FilterOperator.LIKE, "v"));

        SearchQuery q = new SearchQuery(original);

        // Modificar a lista original não afeta a consulta.
        original.add(new FilterCriteria("x", FilterOperator.LIKE, "y"));
        assertEquals(1, q.criteria().size());

        // A lista retornada também deve ser imutável
        assertThrows(UnsupportedOperationException.class, () -> q.criteria().add(
            new FilterCriteria("z", FilterOperator.LIKE, "w")
        ));
    }

    @Test
    @DisplayName("empty() retorna criteria vazia e imutável")
    void emptyFactoryReturnsEmptyImmutable() {
        SearchQuery q = SearchQuery.empty();
        assertTrue(q.criteria().isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> q.criteria().add(
            new FilterCriteria("a", FilterOperator.LIKE, "b")
        ));
    }

    @Test
    @DisplayName("Construtor trata null como lista vazia")
    void nullCriteriaIsTreatedAsEmpty() {
        SearchQuery q = new SearchQuery(null);
        assertTrue(q.criteria().isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> q.criteria().add(
            new FilterCriteria("a", FilterOperator.LIKE, "b")
        ));
    }
}

