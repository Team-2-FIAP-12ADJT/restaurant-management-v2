package com.fiap.restaurant_management_v2.infrastructure.web;

import com.fiap.restaurant_management_v2.application.exception.DuplicateUserException;
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
