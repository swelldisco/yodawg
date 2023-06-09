package com.example.app_tracker.app_backend.exceptions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler{
    
    @ExceptionHandler(AppNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleAppNotFoundException(AppNotFoundException ex, WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(
            LocalDateTime.now(), 
            ex.getMessage(), 
            webRequest.getDescription(false), 
            "APPLICATION_NOT_FOUND");
        return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RepositoryEmptyException.class)
    public ResponseEntity<ErrorDetails> handleRepositoryEmptyException(AppNotFoundException ex, WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(
            LocalDateTime.now(),
            ex.getMessage(),
            webRequest.getDescription(false),
            "REPOSITORY_EMPTY"
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleException(AppNotFoundException ex, WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(
            LocalDateTime.now(),
            ex.getMessage(),
            webRequest.getDescription(false),
            "INTERNAL_SERVER_ERROR"
        );
        return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> validationErrors = new ArrayList<>();
        List<ObjectError> errorList = ex.getBindingResult().getAllErrors();
        errorList.stream()
                .forEach(e -> {
                    String fieldName = ((FieldError)e).getField();
                    String message = e.getDefaultMessage();
                    validationErrors.add(fieldName + ": " + message);
                });
        return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
    }

}
