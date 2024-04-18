package com.springproject.core.Controllers;

import com.springproject.core.exceptions.CoverNotFoundException;
import com.springproject.core.exceptions.InvalidAuthException;
import com.springproject.core.model.dto.ErrorMessageDTO;
import com.springproject.core.exceptions.BookNotFoundException;
import com.springproject.core.exceptions.InvalidBookTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BookNotFoundException.class, InvalidBookTypeException.class,
            CoverNotFoundException.class, InvalidAuthException.class})
    public ErrorMessageDTO handleBadRequest(RuntimeException exception) {
        return new ErrorMessageDTO(exception.getMessage());
    }
}
