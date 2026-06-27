package com.bluesoftware.petshop.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 🔹 VALIDACIONES (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();

        ApiError error = new ApiError();
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setError("VALIDATION_ERROR");
        error.setMessage("Validation failed");
        error.setDetails(errors);
        error.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 🔹 ENUM / JSON MAL FORMADO (BODY)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleJsonErrors(HttpMessageNotReadableException ex) {

        ApiError error = new ApiError();
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setError("BAD_REQUEST");
        error.setTimestamp(LocalDateTime.now());

        // Detectar enum inválido
        if (ex.getCause() instanceof InvalidFormatException ife) {

            Class<?> targetType = ife.getTargetType();

            if (targetType.isEnum()) {
                Object[] allowedValues = targetType.getEnumConstants();

                error.setMessage("Invalid value for enum: " + targetType.getSimpleName());
                error.setDetails(allowedValues);

                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }
        }

        error.setMessage("Malformed JSON request");

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 🔹 ENUM / PARAM MAL (query params, path variables)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        ApiError error = new ApiError();
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setError("BAD_REQUEST");
        error.setTimestamp(LocalDateTime.now());

        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {

            Object[] allowedValues = ex.getRequiredType().getEnumConstants();

            error.setMessage("Invalid value for parameter: " + ex.getName());
            error.setDetails(allowedValues);

            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        error.setMessage("Invalid parameter type");

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 🔹 NOT FOUND
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {

        ApiError error = new ApiError();
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setError("NOT_FOUND");
        error.setMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // 🔹 BAD REQUEST (custom)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {

        ApiError error = new ApiError();
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setError("BAD_REQUEST");
        error.setMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 🔹 BAD CREDENTIALS (401)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex) {

        ApiError error = new ApiError();
        error.setStatus(HttpStatus.UNAUTHORIZED.value());
        error.setError("UNAUTHORIZED");
        error.setMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    // 🔹 ACCESS DENIED (403)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {

        ApiError error = new ApiError();
        error.setStatus(HttpStatus.FORBIDDEN.value());
        error.setError("FORBIDDEN");
        error.setMessage("Acceso denegado");
        error.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    // 🔹 ERROR GENERAL (fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(Exception ex) {

        ex.printStackTrace(); // 🔥 luego reemplazás por logger

        ApiError error = new ApiError();
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setError("INTERNAL_SERVER_ERROR");
        error.setMessage("Unexpected error occurred");
        error.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}