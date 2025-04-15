package faang.school.postservice.exception.handler;

import faang.school.postservice.exception.InvalidFileException;
import faang.school.postservice.exception.InvalidPostAuthorsException;
import faang.school.postservice.exception.LanguageToolException;
import faang.school.postservice.exception.MaxResourcesReachedException;
import faang.school.postservice.exception.PostIdMismatchException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.exception.minio_exceptions.BucketNotFoundException;
import faang.school.postservice.exception.minio_exceptions.MinioRemovingFileException;
import faang.school.postservice.exception.minio_exceptions.MinioUploadingFileException;
import faang.school.postservice.exception.not_found_exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LanguageToolException.class)
    public ResponseEntity<ErrorResponse> handleLanguageToolException(LanguageToolException exception) {
        String message = "We encountered an issue while checking your post text. Please try again later.";
        ErrorResponse errorResponse = ErrorResponse.builder(exception, exception.getStatusCode(),
                        exception.getMessage())
                .title("Text Validation Service Unavailable")
                .detail(message)
                .property("service", "LanguageTool")
                .build();
        return new ResponseEntity<>(errorResponse, exception.getStatusCode());
    }

    @ExceptionHandler(InvalidPostAuthorsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPostAuthorsException(InvalidPostAuthorsException exception) {
        String message = "A post cannot have both a user and a project as authors." +
                " Please specify only one author (either a user or a project)";
        ErrorResponse errorResponse = ErrorResponse.builder(exception, HttpStatus.BAD_REQUEST, exception.getMessage())
                .title("Invalid Post Author")
                .detail(message)
                .property("service", "PostService")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePostNotFoundException(PostNotFoundException exception) {
        String message = "The post you're looking for doesn't exist or may have been deleted.";
        ErrorResponse errorResponse = ErrorResponse.builder(exception, HttpStatus.NOT_FOUND,
                        exception.getMessage())
                .title("Post Not Found")
                .detail(message)
                .property("service", "PostService")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        String message = "Your request contains invalid data. Please check your input and try again.";
        ErrorResponse errorResponse = ErrorResponse.builder(exception, HttpStatus.BAD_REQUEST,
                        exception.getMessage())
                .title("Invalid Request")
                .detail(message)
                .property("service", "PostService")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxResourcesReachedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public faang.school.postservice.exception.ErrorResponse handleMaxResourcesReachedException(MaxResourcesReachedException e) {
        return faang.school.postservice.exception.ErrorResponse.builder()
                .message(e.getMessage())
                .origin("PostResourceService")
                .errorCode("MAX_RESOURCES_REACHED")
                .timestamp(Instant.now())
                .build();
    }

    @ExceptionHandler(InvalidFileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public faang.school.postservice.exception.ErrorResponse handleInvalidFileException(InvalidFileException e) {
        return faang.school.postservice.exception.ErrorResponse.builder()
                .message(e.getMessage())
                .origin("PostResourceService")
                .errorCode("INVALID_FILE")
                .timestamp(Instant.now())
                .build();
    }

    @ExceptionHandler(BucketNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public faang.school.postservice.exception.ErrorResponse handleBucketNotFoundException(BucketNotFoundException e) {
        return faang.school.postservice.exception.ErrorResponse.builder()
                .message(e.getMessage())
                .origin("MinioService")
                .errorCode("BUCKET_NOT_FOUND")
                .timestamp(Instant.now())
                .build();
    }

    @ExceptionHandler(MinioRemovingFileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public faang.school.postservice.exception.ErrorResponse handleMinioRemovingFileException(MinioRemovingFileException e) {
        return faang.school.postservice.exception.ErrorResponse.builder()
                .message(e.getMessage())
                .origin("MinioService")
                .errorCode("MINIO_REMOVING_FILE")
                .timestamp(Instant.now())
                .build();
    }

    @ExceptionHandler(MinioUploadingFileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public faang.school.postservice.exception.ErrorResponse handleMinioUploadingFileException(MinioUploadingFileException e) {
        return faang.school.postservice.exception.ErrorResponse.builder()
                .message(e.getMessage())
                .origin("MinioService")
                .errorCode("MINIO_UPLOADING_FILE")
                .timestamp(Instant.now())
                .build();
    }

    @ExceptionHandler(PostIdMismatchException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public faang.school.postservice.exception.ErrorResponse handleResourcePostIdNotEqualsPostIdException(PostIdMismatchException e) {
        return faang.school.postservice.exception.ErrorResponse.builder()
                .message(e.getMessage())
                .origin("PostResourceService")
                .errorCode("POST_ID_MISMATCH")
                .timestamp(Instant.now())
                .build();
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public faang.school.postservice.exception.ErrorResponse handleResourceNotFoundException(ResourceNotFoundException e) {
        return faang.school.postservice.exception.ErrorResponse.builder()
                .message(e.getMessage())
                .origin("PostResourceService")
                .errorCode("RESOURCE_NOT_FOUND")
                .timestamp(Instant.now())
                .build();
    }
}