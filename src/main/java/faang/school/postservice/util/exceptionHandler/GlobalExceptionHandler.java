package faang.school.postservice.util.exceptionHandler;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException ex) {
        log.error("Data validation exception occurred.", ex);
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException ex) {
        log.error("Not found exception occurred.", ex);
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        StringBuilder message = new StringBuilder();

        ex.getBindingResult().getAllErrors()
                .forEach(error -> message.append(error.getDefaultMessage()).append("\n"));

        log.error("Method argument not valid exception occurred.", ex);
        return new ErrorResponse(message.toString());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(Exception ex) {
        log.error("Exception occurred.", ex);
        return new ErrorResponse(ex.getMessage(), ex.getClass());
    }
}
