package faang.school.postservice.exception.handler;

import faang.school.postservice.exception.externalservice.ExternalServiceConnectException;
import faang.school.postservice.exception.externalservice.ExternalServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ForbiddenException;

import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для всех REST-контроллеров.
 * 400 DataValidationException, IllegalArgumentException
 * 404 EntityNotFoundException
 * 403 ForbiddenException
 * 409 IllegalStateException
 * 500 Exception
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обработка ошибок валидации входных данных.
     * Возвращает JSON с полями, которые не прошли валидацию, и сообщениями из аннотаций.
     * Возвращает 400 Bad Request.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError fieldError
                    ? fieldError.getField() : error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Validation failed for fields: {}", errors.keySet());
        return errors;
    }

    /**
     * Обработка бизнес-ошибок валидации данных.
     * Возвращает 400 Bad Request.
     */
    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException ex) {
        log.warn("Data validation error: {}", ex.getMessage());
        return new ErrorResponse("data_validation_error", ex.getMessage());
    }

    /**
     * Некорректные аргументы вызывающей стороны (ошибка клиента).
     * Возвращает 400 Bad Request.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return new ErrorResponse("bad_request", ex.getMessage());
    }

    @ExceptionHandler(ExternalServiceConnectException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleExternalServiceConnectException(ExternalServiceConnectException ex) {
        String description = "no access to the service: " + ex.getServiceName();
        log.warn(description, ex);
        return new ErrorResponse(description, ex.getMessage());
    }


    /**
     * Обработка Runtime исключений как запасной вариант.
     * Возвращает 400 Bad Request.
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception: {}", ex.getMessage(), ex);
        return new ErrorResponse("runtime_error", ex.getMessage());
    }

    /**
     * Обработка ошибок "сущность не найдена".
     * Возвращает 404 Not Found.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        return new ErrorResponse("entity_not_found", ex.getMessage());
    }

    /**
     * Обработка ошибок доступа.
     * Возвращает 403 Forbidden.
     */
    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenException(ForbiddenException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return new ErrorResponse("forbidden", ex.getMessage());
    }

    /**
     * Конфликт состояния (например, уже опубликован или помечен как удалён).
     * Возвращает 409 Conflict.
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleIllegalStateException(IllegalStateException ex) {
        log.warn("State conflict: {}", ex.getMessage());
        return new ErrorResponse("state_conflict", ex.getMessage());
    }

    /**
     * Запасной вариант для непредвиденных ошибок.
     * Возвращает 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {
        log.error("Unhandled exception: ", ex);
        return new ErrorResponse("internal_server_error", "An unexpected error occurred");
    }

    @ExceptionHandler(ExternalServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleExternalServiceException(ExternalServiceException ex) {
        String description = "no access to the service: " + ex.getServiceName();
        log.warn(description, ex);
        return new ErrorResponse(description, ex.getMessage());
    }
}