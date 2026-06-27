package com.bluesoftware.petshop.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bluesoftware.petshop.exceptions.ApiError;

import java.time.LocalDateTime;

@RestController
@RequestMapping("${server.error.path:${error.path:/error}}")
public class ErrorControllerConfig extends AbstractErrorController {

    public ErrorControllerConfig(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @RequestMapping
    public ResponseEntity<ApiError> handleError(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        ApiError error = new ApiError();
        error.setStatus(status.value());
        error.setError(status.getReasonPhrase().toUpperCase().replace(" ", "_"));
        error.setMessage(status.value() == 404
                ? "La ruta solicitada no existe"
                : "Error del servidor");
        error.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(error, status);
    }
}
