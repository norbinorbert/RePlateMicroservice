package edu.bbte.replate.controlleradvice;

import edu.bbte.replate.exception.BadRequestException;
import edu.bbte.replate.exception.InternalServerErrorException;
import edu.bbte.replate.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException occurred : {}", e.getMessage());
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleBadRequest(BadRequestException e) {
        log.warn("BadRequestException occurred: {}", e.getMessage());
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("Message", e.getMessage());
        return ResponseEntity.badRequest().body(responseBody);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(ResourceNotFoundException e) {
        log.warn("ResourceNotFoundException occurred: {}", e.getMessage());
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("Message", e.getMessage());
        return ResponseEntity.badRequest().body(responseBody);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException e) {
        log.warn("AccessDeniedException occurred: {}", e.getMessage());
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("Message", e.getMessage());
        return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, String>> handleInternalServerError(
            InternalServerErrorException e
    ) {
        log.error("Internal server error occurred: {}", e.getMessage());
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("Message", "An internal server error occurred.");
        return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

