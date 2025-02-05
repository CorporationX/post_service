package faang.school.postservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(BusinessException ex) {
        return new ErrorResponse(
                ex.getMessage(),
                "Business",
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(DataValidationException ex) {
        return new ErrorResponse(
                ex.getMessage(),
                "DataValidation",
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleException(EntityNotFoundException ex) {
        return new ErrorResponse(
                ex.getMessage(),
                "Entity not found",
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(
            MissingServletRequestParameterException ex
    ) {
        return new ErrorResponse(
                "Query параметр не был найден",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponse(
                ex.getMessage(),
                "Непредвиденная ошибка сервера",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}