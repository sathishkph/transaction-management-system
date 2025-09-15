package com.outseer.tms.advice;

import com.outseer.tms.dto.ErrorCode;
import com.outseer.tms.exception.DuplicateTransactionIdException;
import com.outseer.tms.exception.InsufficientBalanceException;
import com.outseer.tms.dto.ErrorResponse;
import com.outseer.tms.exception.UserIdAlreadyExistsException;
import com.outseer.tms.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ExceptionController {
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalance(InsufficientBalanceException ex) {
        ErrorResponse error = new ErrorResponse(ErrorCode.INSUFFICIENT_BALANCE, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(ErrorCode.USER_NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateTransactionIdException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateTransactionId(DuplicateTransactionIdException ex) {
        ErrorResponse error = new ErrorResponse(ErrorCode.DUPLICATE_TRANSACTION, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserIdAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserIdExists(UserIdAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(ErrorCode.USER_ID_EXISTS, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericExceptions(Exception ex) {

        if (ex.getMessage().contains("favicon.ico")) {
            return ResponseEntity.notFound().build();  // Silent 404
        }
        log.error("Internal error occurred: {}", ex.getMessage(), ex);

        ErrorResponse response = new ErrorResponse(
                ErrorCode.INTERNAL_ERROR,
                "An unexpected error occurred. Please try again later.");  // Sanitized message

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Validation failed: {}", errors);

        ErrorResponse response = new ErrorResponse(
                ErrorCode.VALIDATION_ERROR,
                "Validation failed",
                errors
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
