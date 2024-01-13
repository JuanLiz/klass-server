package com.klass.server.common.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BadRequest> handleValidationExceptions(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(new BadRequest(400, e.getBindingResult().getFieldError().getDefaultMessage()));
    }

    // TODO existing email exception

}
