package com.fiap.restaurant_management_v2.infrastructure.web;

import com.fiap.restaurant_management_v2.application.exception.*;
import com.fiap.restaurant_management_v2.domain.exception.*;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        GlobalExceptionHandler.class
    );

    private ProblemDetail problem(
        HttpStatus status,
        String detail,
        Exception ex
    ) {
        LOGGER.warn(
            "{} -> {}: {}",
            ex.getClass().getSimpleName(),
            status.value(),
            detail
        );
        return ProblemDetail.forStatusAndDetail(status, detail);
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ProblemDetail handleDuplicate(DuplicateUserException ex) {
        return problem(HttpStatus.CONFLICT, ex.getMessage(), ex);
    }

    @ExceptionHandler(DuplicateUserTypeException.class)
    public ProblemDetail handleDuplicate(DuplicateUserTypeException ex) {
        return problem(HttpStatus.CONFLICT, ex.getMessage(), ex);
    }

    @ExceptionHandler(DuplicateRestaurantException.class)
    public ProblemDetail handleDuplicate(DuplicateRestaurantException ex) {
        return problem(HttpStatus.CONFLICT, ex.getMessage(), ex);
    }

    @ExceptionHandler(UserHasActiveRestaurantsException.class)
    public ProblemDetail handleUserHasRestaurants(
        UserHasActiveRestaurantsException ex
    ) {
        return problem(HttpStatus.CONFLICT, ex.getMessage(), ex);
    }

    @ExceptionHandler(
        org.springframework.dao.DataIntegrityViolationException.class
    )
    public ProblemDetail handleDataIntegrityViolation(
        org.springframework.dao.DataIntegrityViolationException ex
    ) {
        return problem(
            HttpStatus.CONFLICT,
            "Violacao de integridade: registro duplicado ou referencia invalida",
            ex
        );
    }

    @ExceptionHandler(InvalidUserException.class)
    public ProblemDetail handleInvalid(InvalidUserException ex) {
        return problem(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }

    @ExceptionHandler(InvalidUserTypeException.class)
    public ProblemDetail handleInvalid(InvalidUserTypeException ex) {
        return problem(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }

    @ExceptionHandler(InvalidRestaurantException.class)
    public ProblemDetail handleRestaurantInvalid(
        InvalidRestaurantException ex
    ) {
        return problem(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }

    @ExceptionHandler(InvalidUserTypeUuidException.class)
    public ProblemDetail handleInvalid(InvalidUserTypeUuidException ex) {
        return problem(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }

    @ExceptionHandler(InvalidFilterException.class)
    public ProblemDetail handleInvalidFilter(InvalidFilterException ex) {
        return problem(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleNotFound(UserNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
    }

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ProblemDetail restaurantNotFound(RestaurantNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
    }

    @ExceptionHandler(UserTypeNotFoundException.class)
    public ProblemDetail handleNotFound(UserTypeNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
    }

    @ExceptionHandler(MenuItemNotFoundException.class)
    public ProblemDetail menuItemNotFound(MenuItemNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
    }

    @ExceptionHandler(InvalidMenuItemException.class)
    public ProblemDetail handleMenuItemInvalid(InvalidMenuItemException ex) {
        return problem(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        return problem(
            HttpStatus.BAD_REQUEST,
            "Parâmetro inválido: " + ex.getMessage(),
            ex
        );
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) throws Exception {
        // Exceção com status próprio (ErrorResponse — inclui
        // ResponseStatusException — ou @ResponseStatus): rethrow deixa os
        // resolvers do Spring aplicá-lo em vez de forçar 500.
        if (
            ex instanceof org.springframework.web.ErrorResponse ||
            org.springframework.core.annotation.AnnotationUtils.findAnnotation(
                ex.getClass(),
                org.springframework.web.bind.annotation.ResponseStatus.class
            ) != null
        ) {
            throw ex;
        }
        LOGGER.error("unhandled exception -> 500", ex);
        return ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Erro interno inesperado"
        );
    }

    // Exceções PADRÃO do Spring MVC: com `problemdetails.enabled=true` o handler
    // interno do framework ofusca @ExceptionHandler avulsos. O jeito confiável é
    // estender ResponseEntityExceptionHandler e sobrescrever os métodos protegidos.

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
            .getFieldErrors()
            .forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
            );
        LOGGER.warn(
            "MethodArgumentNotValidException -> {}: {}",
            status.value(),
            errors
        );
        ProblemDetail body = ex.getBody();
        body.setDetail("Falha de validação");
        body.setProperty("errors", errors);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
        HttpMessageNotReadableException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
    ) {
        LOGGER.warn(
            "HttpMessageNotReadableException -> {}: corpo inválido ou malformado",
            status.value()
        );
        ProblemDetail body = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Corpo da requisição inválido ou malformado"
        );
        return handleExceptionInternal(ex, body, headers, status, request);
    }
}
