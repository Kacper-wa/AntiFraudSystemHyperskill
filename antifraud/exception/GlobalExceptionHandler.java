package antifraud.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    ResponseEntity<Exception> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
