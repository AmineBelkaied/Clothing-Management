package com.clothing.management.exceptions;

import com.clothing.management.exceptions.generic.AlreadyExistsException;
import com.clothing.management.exceptions.generic.EntityNotFoundException;
import com.clothing.management.models.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle AlreadyExistsException
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExistsException(AlreadyExistsException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                request.getDescription(false),
                HttpStatus.CONFLICT.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // Handle EntityNotFoundException
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                request.getDescription(false),
                HttpStatus.NOT_FOUND.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // Generic Exception Handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                "An unexpected error occurred : " + ex.getMessage(),
                request.getDescription(false),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}