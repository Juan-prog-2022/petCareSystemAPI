package com.bluesoftware.petshop.exceptions;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiError {
    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
    private Object details;
}
