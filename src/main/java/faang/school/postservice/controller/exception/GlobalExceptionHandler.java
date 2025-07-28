package faang.school.postservice.controller.exception;

import faang.school.postservice.dto.error.ErrorResponse;
import faang.school.postservice.dto.error.ValidationErrorDetail;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.exception.ServiceUnavailableException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFound(EntityNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        return ErrorResponse.of("Entity not found", ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolation(ConstraintViolationException ex) {
        log.error("Constraint violation", ex);
        List<ValidationErrorDetail> details = ex.getConstraintViolations().stream()
                .map(this::mapToValidationErrorDetail)
                .toList();
        return ErrorResponse.withDetails(
                "Constraint violation",
                "Validation failed for one or more fields.",
                details
        );
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbidden(ForbiddenException ex) {
        log.error(ex.getMessage(), ex);
        return ErrorResponse.of("Forbidden", ex.getMessage());
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleServiceUnavailable(ServiceUnavailableException ex) {
        log.error(ex.getMessage(), ex);
        return ErrorResponse.of("Service unavailable", ex.getMessage());
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidation(DataValidationException ex) {
        log.error(ex.getMessage(), ex);
        return ErrorResponse.of("Data validation exception", ex.getMessage());
    }

    private ValidationErrorDetail mapToValidationErrorDetail(ConstraintViolation<?> violation) {
        String fieldName = extractFieldName(violation.getPropertyPath());
        return new ValidationErrorDetail(
                fieldName,
                violation.getMessage(),
                violation.getInvalidValue()
        );
    }

    private String extractFieldName(Path propertyPath) {
        String fieldName = null;
        for (Path.Node node : propertyPath) {
            fieldName = node.getName();
        }
        return fieldName;
    }
}
