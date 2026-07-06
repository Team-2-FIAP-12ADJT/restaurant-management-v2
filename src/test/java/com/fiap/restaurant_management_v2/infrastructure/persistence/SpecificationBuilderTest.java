package com.fiap.restaurant_management_v2.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fiap.restaurant_management_v2.application.exception.InvalidFilterException;
import com.fiap.restaurant_management_v2.application.gateways.search.FilterCriteria;
import com.fiap.restaurant_management_v2.application.gateways.search.FilterOperator;
import com.fiap.restaurant_management_v2.application.gateways.search.SearchQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

class SpecificationBuilderTest {

    @Test
    @DisplayName("Throws when field is not allowed")
    void throwsWhenFieldNotAllowed() {
        var criteria = new FilterCriteria("bad", FilterOperator.LIKE, "x");
        var query = new SearchQuery(List.of(criteria));

        var allowed = Map.of("name", "name");

        assertThrows(
            InvalidFilterException.class,
            () -> SpecificationBuilder.build(query, allowed)
        );
    }

    @Test
    @DisplayName("LIKE operator delegates to CriteriaBuilder.like")
    void likeOperatorCallsCbLike() {
        var criteria = new FilterCriteria("name", FilterOperator.LIKE, "AbC");
        var query = new SearchQuery(List.of(criteria));
        var allowed = Map.of("name", "name");

        Specification<Object> spec = SpecificationBuilder.build(query, allowed);

        Root<Object> root = mockRoot();
        CriteriaQuery<Object> cq = mockCriteriaQuery();
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Path<String> path = mockPath();
        Expression<String> lower = mockExpression();
        Predicate predicate = mock(Predicate.class);

        when(root.<String>get("name")).thenReturn(path);
        when(cb.lower(path)).thenReturn(lower);
        when(cb.like(lower, "%abc%")).thenReturn(predicate);

        Predicate result = spec.toPredicate(root, cq, cb);

        assertEquals(predicate, result);
        verify(cb).like(lower, "%abc%");
    }

    @Test
    @DisplayName("EQUALS operator delegates to CriteriaBuilder.equal")
    void equalsOperatorCallsCbEqual() {
        var criteria = new FilterCriteria(
            "name",
            FilterOperator.EQUALS,
            "value"
        );
        var query = new SearchQuery(List.of(criteria));
        var allowed = Map.of("name", "name");

        Specification<Object> spec = SpecificationBuilder.build(query, allowed);

        Root<Object> root = mockRoot();
        CriteriaQuery<Object> cq = mockCriteriaQuery();
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Path<String> path = mockPath();
        Predicate predicate = mock(Predicate.class);

        when(root.<String>get("name")).thenReturn(path);
        when(cb.equal(path, "value")).thenReturn(predicate);

        Predicate result = spec.toPredicate(root, cq, cb);

        assertEquals(predicate, result);
        verify(cb).equal(path, "value");
    }

    @Test
    @DisplayName("Filters out criteria with null value (PATCH omitted fields)")
    void filtersOutNullValues() {
        var criteria1 = new FilterCriteria(
            "name",
            FilterOperator.LIKE,
            "value"
        );
        var criteria2 = new FilterCriteria(
            "email",
            FilterOperator.LIKE,
            null
        );
        var query = new SearchQuery(List.of(criteria1, criteria2));
        var allowed = Map.of("name", "name", "email", "email");

        Specification<Object> spec = SpecificationBuilder.build(query, allowed);

        Root<Object> root = mockRoot();
        CriteriaQuery<Object> cq = mockCriteriaQuery();
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Path<String> path = mockPath();
        Expression<String> lower = mockExpression();
        Predicate predicate = mock(Predicate.class);

        when(root.<String>get("name")).thenReturn(path);
        when(cb.lower(path)).thenReturn(lower);
        when(cb.like(lower, "%value%")).thenReturn(predicate);

        Predicate result = spec.toPredicate(root, cq, cb);

        assertEquals(predicate, result);
        verify(cb).like(lower, "%value%");
    }

    @Test
    @DisplayName("Filters out criteria with blank (whitespace-only) value")
    void filtersOutBlankValues() {
        var criteria1 = new FilterCriteria(
            "name",
            FilterOperator.LIKE,
            "value"
        );
        var criteria2 = new FilterCriteria(
            "email",
            FilterOperator.LIKE,
            "   "
        );
        var query = new SearchQuery(List.of(criteria1, criteria2));
        var allowed = Map.of("name", "name", "email", "email");

        Specification<Object> spec = SpecificationBuilder.build(query, allowed);

        Root<Object> root = mockRoot();
        CriteriaQuery<Object> cq = mockCriteriaQuery();
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Path<String> path = mockPath();
        Expression<String> lower = mockExpression();
        Predicate predicate = mock(Predicate.class);

        when(root.<String>get("name")).thenReturn(path);
        when(cb.lower(path)).thenReturn(lower);
        when(cb.like(lower, "%value%")).thenReturn(predicate);

        Predicate result = spec.toPredicate(root, cq, cb);

        assertEquals(predicate, result);
        verify(cb).like(lower, "%value%");
    }

    @Test
    @DisplayName("Empty query (all criteria filtered out) matches all records")
    void emptyQueryAfterFilteringMatchesAll() {
        var criteria = new FilterCriteria("name", FilterOperator.LIKE, null);
        var query = new SearchQuery(List.of(criteria));
        var allowed = Map.of("name", "name");

        Specification<Object> spec = SpecificationBuilder.build(query, allowed);

        Root<Object> root = mockRoot();
        CriteriaQuery<Object> cq = mockCriteriaQuery();
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Predicate result = spec.toPredicate(root, cq, cb);

        assertNull(result);
    }

    @SuppressWarnings("unchecked")
    private static <T> Root<T> mockRoot() {
        return (Root<T>) mock(Root.class);
    }

    @SuppressWarnings("unchecked")
    private static <T> CriteriaQuery<T> mockCriteriaQuery() {
        return (CriteriaQuery<T>) mock(CriteriaQuery.class);
    }

    @SuppressWarnings("unchecked")
    private static <T> Path<T> mockPath() {
        return (Path<T>) mock(Path.class);
    }

    @SuppressWarnings("unchecked")
    private static <T> Expression<T> mockExpression() {
        return (Expression<T>) mock(Expression.class);
    }
}
