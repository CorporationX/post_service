package faang.school.postservice.exception;

import faang.school.postservice.exception.dto.ErrorResponse;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation error occured: {}", ex.getMessage(), ex);
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse response = new ErrorResponse("Validation error", "Check input data");
        response.setErrorsList(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("Constraint violation error occured: {}", ex.getMessage(), ex);
        Map<String, String> errors = new HashMap<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        }

        ErrorResponse response = new ErrorResponse("Validation error", "Check input data");
        response.setErrorsList(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<ErrorResponse> handleDataValidationException(DataValidationException ex) {
        log.error("Data validation exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponse(ex.getMessage(), "Data validation error, check input data"));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("Entity not found exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse(ex.getMessage(), "Entity not found, check id"));
    }

    @ExceptionHandler(EntityWasRemovedException.class)
    public ResponseEntity<ErrorResponse> handleEntityWasRemovedException(EntityWasRemovedException ex) {
        log.error("Entity was removed exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage(), "Entity is no longer available"));
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ErrorResponse> handleFeignNotFoundException(FeignException.NotFound ex) {
        log.error("Feign exception: {}", ex.getMessage(), ex);
        String errorMessage = extractMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(errorMessage, "External service returned a 404 error"));
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ErrorResponse> handleJsonProcessingException(JsonProcessingException ex) {
        log.error("Json processing exception: {}", ex.getMessage(), ex);
        String errorMessage = extractMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(errorMessage, "Json processing error"));
    }

    @ExceptionHandler(NetworkException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleNetworkException(NetworkException ex) {
        log.error("Network error occurred: {}", ex.getMessage(), ex);

        String fullMessage = ex.getMessage();
        int lastBracketIndex = fullMessage.lastIndexOf("[");

        if (lastBracketIndex != -1 && lastBracketIndex + 1 < fullMessage.length()) {
            return fullMessage.substring(lastBracketIndex + 1, fullMessage.length() - 1);
        }

        return "Unknown error";
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUserNotFoundException(UserNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage(), ex);

        String fullMessage = ex.getMessage();
        int lastBracketIndex = fullMessage.lastIndexOf("[");

        if (lastBracketIndex != -1 && lastBracketIndex + 1 < fullMessage.length()) {
            return fullMessage.substring(lastBracketIndex + 1, fullMessage.length() - 1);
        }

        return "User not found";
    }

    private String extractMessage(String fullMessage) {
        int lastBracketIndex = fullMessage.lastIndexOf("[");
        if (lastBracketIndex != -1 && lastBracketIndex + 1 < fullMessage.length()) {
            return fullMessage.substring(lastBracketIndex + 1, fullMessage.length() - 1);
        }
        return "Unknown error";
    }

    @ExceptionHandler(NotUniqueAlbumException.class)
    public ResponseEntity<String> handleNotUniqueAlbumException(NotUniqueAlbumException ex) {
        log.error("NotUniqueAlbumException access exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(AdNotFoundException.class)
    public ResponseEntity<String> handleAdNotFoundException(AdNotFoundException ex) {
        log.error("AdNotFoundException occurred while accessing ad: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(FileDeletionException.class)
    public ResponseEntity<String> handleFileDeletionException(FileDeletionException ex) {
        log.error("FileDeletionException occurred while deleting file: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(FileException.class)
    public ResponseEntity<String> handleFileException(FileException ex) {
        log.error("FileException occurred while accessing file: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(ResizeFileException.class)
    public ResponseEntity<String> handleResizeFileException(ResizeFileException ex) {
        log.error("ResizeFileException occurred while resizing file: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(UnsupportedResourceException.class)
    public ResponseEntity<String> handleUnsupportedResourceException(UnsupportedResourceException ex) {
        log.error("UnsupportedResourceException occurred while accessing resource: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}