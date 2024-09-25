package faang.school.postservice.exception;

import faang.school.postservice.exception.dto.ErrorResponseDto;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponseDto handleIllegalArgumentException(IllegalArgumentException exception) {
        return generateResponse(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ErrorResponseDto handleValidationException(ValidationException exception) {
        return generateResponse(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponseDto handleEntityNotFoundException(EntityNotFoundException exception) {
        return generateResponse(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FeignException.class)
    public ErrorResponseDto handleFeignException(FeignException exception) {
        return generateResponse(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorResponseDto generateResponse(Exception exception, HttpStatus httpStatus) {
        log.error(String.valueOf(exception));
        return new ErrorResponseDto(httpStatus.value(), exception.getMessage());
    }
}
