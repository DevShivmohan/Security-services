package com.shiv.security.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.FileNotFoundException;
import java.io.IOException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(GenericException.class)
    public ResponseEntity<?> handlerGenericException(GenericException genericException){
        log.error(genericException.getMessage());
        genericException.printStackTrace();
        return ResponseEntity.status(genericException.getStatusCode()).body(genericException.getErrorMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handlerIOException(IOException ioException){
        log.error(ioException.getMessage());
        ioException.printStackTrace();
        return ResponseEntity.status(HttpStatus.INSUFFICIENT_STORAGE).body(ioException.getMessage());
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<?> handlerIOException(FileNotFoundException fileNotFoundException){
        log.error(fileNotFoundException.getMessage());
        fileNotFoundException.printStackTrace();
        return ResponseEntity.status(HttpStatus.INSUFFICIENT_STORAGE).body(fileNotFoundException.getMessage());
    }
}
