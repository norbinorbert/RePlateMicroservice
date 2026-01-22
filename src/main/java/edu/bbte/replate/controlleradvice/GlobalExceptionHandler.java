package edu.bbte.replate.controlleradvice;

import edu.bbte.replate.dto.outgoing.SimpleMessageResponseDto;
import edu.bbte.replate.dto.outgoing.ValidationErrorResponseDto;
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ValidationErrorResponseDto> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException occurred : {}", e.getMessage());
        Map<String, String> errors = new ConcurrentHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        var responseBody = new ValidationErrorResponseDto(errors);
        return ResponseEntity.badRequest().body(responseBody);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ValidationErrorResponseDto> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("IllegalArgumentException occurred: {}", e.getMessage());
        Map<String, String> errors = new ConcurrentHashMap<>();
        errors.put("", e.getMessage());
        var responseBody = new ValidationErrorResponseDto(errors);
        return ResponseEntity.badRequest().body(responseBody);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<SimpleMessageResponseDto> handleBadRequest(BadRequestException e) {
        log.warn("BadRequestException occurred: {}", e.getMessage());
        var responseBody = new SimpleMessageResponseDto(e.getMessage());
        return ResponseEntity.badRequest().body(responseBody);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<SimpleMessageResponseDto> handleResourceNotFound(ResourceNotFoundException e) {
        log.warn("ResourceNotFoundException occurred: {}", e.getMessage());
        var responseBody = new SimpleMessageResponseDto(e.getMessage());
        return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<SimpleMessageResponseDto> handleAccessDenied(AccessDeniedException e) {
        log.warn("AccessDeniedException occurred: {}", e.getMessage());
        var responseBody = new SimpleMessageResponseDto(e.getMessage());
        return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<SimpleMessageResponseDto> handleInternalServerError(
            InternalServerErrorException e
    ) {
        log.error("Internal server error occurred: {}", e.getMessage());
        var responseBody = new SimpleMessageResponseDto("An internal server error occurred.");
        return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
