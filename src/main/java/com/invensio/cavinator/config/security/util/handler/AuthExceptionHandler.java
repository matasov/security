package com.invensio.cavinator.config.security.util.handler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class AuthExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        UUID errorId = UUID.randomUUID();
        log.error("Error ID: " + errorId.toString(), ex);
        Map<String, String> response = new LinkedHashMap<>();
        response.put("id", errorId.toString());
        response.put("message", ex.getMessage());
        response.put("type", "RuntimeException");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(NoResourceFoundException ex) {
        UUID errorId = UUID.randomUUID();
        log.error("Error ID: " + errorId.toString(), ex);
        Map<String, String> response = new LinkedHashMap<>();
        response.put("id", errorId.toString());
        response.put("message", ex.getMessage());
        response.put("type", "NoResourceFoundException");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}
