package faang.school.postservice.controller.global;

import faang.school.postservice.dto.error.ErrorResponse;
import faang.school.postservice.dto.error.ValidationError;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ValidationError> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("Exception occurred while handling request at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        
        List<ValidationError> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(new ValidationError(error.getField(), error.getDefaultMessage()));
        }
        return errors;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        log.error("Exception occurred while handling request at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        return new ErrorResponse(
                "Illegal argument: " + ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
        log.error("Exception occurred while handling request at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        return new ErrorResponse(
                "Entity not found: " + ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ErrorResponse handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        log.error("Exception occurred while handling request at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        return new ErrorResponse(
                "File size exceeds limit: " + ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ValidationError> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        log.error("Constraint violation at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        List<ValidationError> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String field = violation.getPropertyPath().toString();
            String description = violation.getMessage();
            errors.add(new ValidationError(field, description));
        }
        
        return errors;
    }

    @ExceptionHandler({ Throwable.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneralException(Throwable ex, HttpServletRequest request) {
        log.error("Exception occurred while handling request at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        return new ErrorResponse(
                "Exception: " + ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
    }
}
