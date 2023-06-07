package com.demo.pms.rest;

import com.demo.pms.exceptions.NoDataFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/*
* ProductManagementExceptionHandler is a class that handles exceptions
* thrown by the application and returns a well-structured response
* */
@ControllerAdvice
@Slf4j
public class ProductManagementExceptionHandler {

    @ExceptionHandler(NoDataFoundException.class)
    protected ResponseEntity<Object> handleNoDataFoundException(NoDataFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(
                Arrays.stream(Objects.requireNonNull(e.getDetailMessageArguments()))
                        .map(ArrayList.class::cast)
                        .filter(o -> !o.isEmpty())
                        .flatMap(ArrayList::stream)
                        .collect(Collectors.toList())
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<Object> handleValidationException(DataIntegrityViolationException e) {
        log.error(e.getMessage(), e);
        if (e.getMessage().contains("duplicate key value violates unique constraint \"product_code_key\"")) {
            return ResponseEntity.badRequest().body(
                    List.of("code: 'Product with this code already exists'")
            );
        }
        return ResponseEntity.badRequest().build();
    }
}
