package com.springproject.core.Controllers;

import com.springproject.core.exceptions.*;
import com.springproject.core.model.dto.ErrorMessageDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionController {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BookNotFoundException.class, InvalidBookTypeException.class,
            CoverNotFoundException.class, InvalidAuthException.class,
            AccessDeniedException.class, SaveFileException.class, EntityNotFoundException.class})
    public ErrorMessageDTO handleBadRequest(RuntimeException exception) {
        return new ErrorMessageDTO(exception.getMessage());
    }
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({UsernameNotFoundException.class, ReviewNotFoundException.class})
    public ErrorMessageDTO handleNotFound(RuntimeException exception) {
        return new ErrorMessageDTO(exception.getMessage());
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({AuthenticationException.class})
    public ErrorMessageDTO handleAuthException() {
        return new ErrorMessageDTO("Incorrect username or password");
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorMessageDTO handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        /*Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });*/
        return new ErrorMessageDTO(ex.getBindingResult()
                .getAllErrors().get(0).getDefaultMessage());
    }
}
