package com.klass.server.common.exceptions;

import com.mongodb.MongoWriteException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(
                400,
                e.getBindingResult().getFieldError().getDefaultMessage()));
    }

    @ExceptionHandler(MongoWriteException.class)
    public ResponseEntity<ErrorResponse> handleMongoWriteExceptions(MongoWriteException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(400, e.getError().getMessage()));
    }
}
