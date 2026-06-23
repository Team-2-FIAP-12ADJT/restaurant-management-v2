package com.fiap.restaurant_management_v2.infrastructure.web;

import com.fiap.restaurant_management_v2.application.exception.DuplicateUserException;
import com.fiap.restaurant_management_v2.application.exception.InvalidFilterException;
import com.fiap.restaurant_management_v2.application.exception.MenuItemNotFoundException;
import com.fiap.restaurant_management_v2.application.exception.RestaurantNotFoundException;
import com.fiap.restaurant_management_v2.application.exception.UserNotFoundException;
import com.fiap.restaurant_management_v2.domain.exception.InvalidMenuItemException;
import com.fiap.restaurant_management_v2.domain.exception.InvalidRestaurantException;
import com.fiap.restaurant_management_v2.domain.exception.InvalidUserException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DuplicateUserException.class)
    public ProblemDetail handleDuplicate(DuplicateUserException ex) {
        return ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            ex.getMessage()
        );
    }

    @ExceptionHandler(InvalidUserException.class)
    public ProblemDetail handleInvalid(InvalidUserException ex) {
        return ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.getMessage()
        );
    }

    @ExceptionHandler(InvalidRestaurantException.class)
    public ProblemDetail handleRestaurantInvalid(InvalidRestaurantException ex) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    @ExceptionHandler(InvalidFilterException.class)
    public ProblemDetail handleInvalidFilter(InvalidFilterException ex) {
        return ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.getMessage()
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleNotFound(UserNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ProblemDetail restaurantNotFound(RestaurantNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    @ExceptionHandler(MenuItemNotFoundException.class)
    public ProblemDetail menuItemNotFound(MenuItemNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );
    }

    @ExceptionHandler(InvalidMenuItemException.class)
    public ProblemDetail handleMenuItemInvalid(InvalidMenuItemException ex) {
        return ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Falha de validação"
        );
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
            .getFieldErrors()
            .forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
            );
        problem.setProperty("errors", errors);
        return problem;
    }
}
