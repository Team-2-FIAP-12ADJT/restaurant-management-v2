package com.fiap.restaurant_management_v2.infrastructure.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.fiap.restaurant_management_v2.application.exception.DuplicateRestaurantException;
import com.fiap.restaurant_management_v2.application.exception.InvalidFilterException;
import com.fiap.restaurant_management_v2.application.exception.RestaurantNotFoundException;
import com.fiap.restaurant_management_v2.application.exception.UserHasActiveRestaurantsException;
import com.fiap.restaurant_management_v2.application.exception.UserNotFoundException;
import com.fiap.restaurant_management_v2.application.exception.UserTypeNotFoundException;
import com.fiap.restaurant_management_v2.domain.exception.InvalidMenuItemException;
import com.fiap.restaurant_management_v2.domain.exception.InvalidRestaurantException;
import com.fiap.restaurant_management_v2.domain.exception.InvalidUserTypeException;
import com.fiap.restaurant_management_v2.domain.exception.InvalidUserTypeUuidException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

class GlobalExceptionHandlerTest {

    @Test
    void handleUnexpectedReturns500WithoutLeakingDetail() throws Exception {
        var handler = new GlobalExceptionHandler();

        var pd = handler.handleUnexpected(new RuntimeException("segredo interno"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), pd.getStatus());
        assertEquals("Erro interno inesperado", pd.getDetail());
    }

    @Test
    void handleUnexpectedRethrowsResponseStatusAnnotated() {
        var handler = new GlobalExceptionHandler();
        var annotated = new TeapotException();

        var thrown = org.junit.jupiter.api.Assertions.assertThrows(
            TeapotException.class,
            () -> handler.handleUnexpected(annotated)
        );
        assertEquals(annotated, thrown);
    }

    @org.springframework.web.bind.annotation.ResponseStatus(
        HttpStatus.I_AM_A_TEAPOT
    )
    private static final class TeapotException extends RuntimeException {}

    @Test
    void handleUnexpectedRethrowsErrorResponse() {
        var handler = new GlobalExceptionHandler();
        var rse = new org.springframework.web.server.ResponseStatusException(
            HttpStatus.NOT_FOUND
        );

        var thrown = org.junit.jupiter.api.Assertions.assertThrows(
            org.springframework.web.server.ResponseStatusException.class,
            () -> handler.handleUnexpected(rse)
        );
        assertEquals(rse, thrown);
    }

    @Test
    void handleIllegalArgumentReturnsBadRequestDetail() {
        var handler = new GlobalExceptionHandler();

        var pd = handler.handleIllegalArgument(new IllegalArgumentException("uuid"));

        assertEquals(HttpStatus.BAD_REQUEST.value(), pd.getStatus());
        assert pd.getDetail() != null;
        assertTrue(pd.getDetail().contains("uuid"));
    }

    @Test
    void mapsRemainingApplicationAndDomainExceptions() {
        var handler = new GlobalExceptionHandler();

        assertProblem(handler.handleDuplicate(new DuplicateRestaurantException("duplicate")), HttpStatus.CONFLICT, "duplicate");
        assertProblem(handler.handleUserHasRestaurants(new UserHasActiveRestaurantsException("active")), HttpStatus.CONFLICT, "active");
        assertProblem(handler.handleDataIntegrityViolation(new org.springframework.dao.DataIntegrityViolationException("dup")), HttpStatus.CONFLICT, "Violacao de integridade");
        assertProblem(handler.handleInvalid(new InvalidUserTypeException("invalid type")), HttpStatus.BAD_REQUEST, "invalid type");
        assertProblem(handler.handleRestaurantInvalid(new InvalidRestaurantException("invalid restaurant")), HttpStatus.BAD_REQUEST, "invalid restaurant");
        assertProblem(handler.handleInvalid(new InvalidUserTypeUuidException("invalid uuid")), HttpStatus.BAD_REQUEST, "invalid uuid");
        assertProblem(handler.handleInvalidFilter(new InvalidFilterException("invalid filter")), HttpStatus.BAD_REQUEST, "invalid filter");
        assertProblem(handler.handleNotFound(new UserNotFoundException("missing user")), HttpStatus.NOT_FOUND, "missing user");
        assertProblem(handler.restaurantNotFound(new RestaurantNotFoundException("missing restaurant")), HttpStatus.NOT_FOUND, "missing restaurant");
        assertProblem(handler.handleNotFound(new UserTypeNotFoundException("missing type")), HttpStatus.NOT_FOUND, "missing type");
    }

    @Test
    void handleMenuItemInvalidReturnsBadRequestDetail() {
        var handler = new GlobalExceptionHandler();

        var problem = handler.handleMenuItemInvalid(
            new InvalidMenuItemException("item inválido")
        );

        assertProblem(problem, HttpStatus.BAD_REQUEST, "item inválido");
    }

    @Test
    void handleMethodArgumentNotValidBuildsErrorsMap() throws NoSuchMethodException {
        var handler = new GlobalExceptionHandler();
        WebRequest request = mock(WebRequest.class);

        var target = new Object();
        var binding = new BeanPropertyBindingResult(target, "object");
        binding.addError(new FieldError("object", "name", "must not be blank"));

        var method = this.getClass().getDeclaredMethod("dummy");
        var param = new org.springframework.core.MethodParameter(method, -1);
        var ex = new MethodArgumentNotValidException(param, binding);

        ResponseEntity<Object> resp = handler.handleMethodArgumentNotValid(
            ex,
            new HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            request
        );

        assertNotNull(resp);
        ProblemDetail pd = assertInstanceOf(ProblemDetail.class, resp.getBody());
        var properties = pd.getProperties();
        assertNotNull(properties);
        assertEquals("Falha de validação", pd.getDetail());
        assertEquals(
            java.util.Map.of("name", "must not be blank"),
            properties.get("errors")
        );
    }

    @Test
    void handleHttpMessageNotReadableReturnsBadRequest() {
        var handler = new GlobalExceptionHandler();
        WebRequest request = mock(WebRequest.class);

        ResponseEntity<Object> resp = handler.handleHttpMessageNotReadable(
            new HttpMessageNotReadableException("bad body", emptyInputMessage()),
            new HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            request
        );

        assertNotNull(resp);
        ProblemDetail pd = assertInstanceOf(ProblemDetail.class, resp.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.value(), pd.getStatus());
        assertEquals("Corpo da requisição inválido ou malformado", pd.getDetail());
    }

    private void dummy() {}

    private static HttpInputMessage emptyInputMessage() {
        return new HttpInputMessage() {
            @Override
            public @NonNull InputStream getBody() {
                return new ByteArrayInputStream(new byte[0]);
            }

            @Override
            public @NonNull HttpHeaders getHeaders() {
                return new HttpHeaders();
            }
        };
    }

    private static void assertProblem(
        ProblemDetail problemDetail,
        HttpStatus status,
        String detailFragment
    ) {
        assertEquals(status.value(), problemDetail.getStatus());
        assertNotNull(problemDetail.getDetail());
        assertTrue(problemDetail.getDetail().contains(detailFragment));
    }
}
