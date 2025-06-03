package faang.school.postservice.exception.handler;

import faang.school.postservice.exception.CacheOperationException;
import faang.school.postservice.exception.CacheWarmingException;
import faang.school.postservice.exception.DataRetrievalException;
import faang.school.postservice.exception.InvalidPostAuthorsException;
import faang.school.postservice.exception.LanguageToolException;
import faang.school.postservice.exception.PostDetailException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.exception.PostRetrievalException;
import faang.school.postservice.exception.ServiceUnavailableException;
import faang.school.postservice.exception.UserRetrievalException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

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

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailableException(ServiceUnavailableException exception) {
        String message = "One of the required services is currently unavailable. Please try again later.";
        ErrorResponse errorResponse = ErrorResponse.builder(exception, HttpStatus.SERVICE_UNAVAILABLE,
                        exception.getMessage())
                .title("Service Unavailable")
                .detail(message)
                .property("service", "FeedService")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(CacheOperationException.class)
    public ResponseEntity<ErrorResponse> handleCacheOperationException(CacheOperationException exception) {
        String message = "Failed to perform cache operation. Please try again later.";
        ErrorResponse errorResponse = ErrorResponse.builder(exception, HttpStatus.INTERNAL_SERVER_ERROR,
                        exception.getMessage())
                .title("Cache Operation Failed")
                .detail(message)
                .property("service", "Redis")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CacheWarmingException.class)
    public ResponseEntity<ErrorResponse> handleCacheWarmingException(CacheWarmingException exception) {
        String message = "Failed to warm up cache. Please try again later.";
        ErrorResponse errorResponse = ErrorResponse.builder(exception, HttpStatus.INTERNAL_SERVER_ERROR,
                        exception.getMessage())
                .title("Cache Warming Failed")
                .detail(message)
                .property("service", "FeedService")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataRetrievalException.class)
    public ResponseEntity<ErrorResponse> handleDataRetrievalException(DataRetrievalException exception) {
        String title = "Data Retrieval Error";
        String detail = "Failed to retrieve required data. Please try again later.";

        if (exception instanceof PostRetrievalException) {
            title = "Post Retrieval Failed";
            detail = "Could not retrieve post data. Please try again later.";
        } else if (exception instanceof UserRetrievalException) {
            title = "User Retrieval Failed";
            detail = "Could not retrieve user information. Please try again later.";
        } else if (exception instanceof PostDetailException) {
            title = "Post Details Unavailable";
            detail = "Could not retrieve post details. Please try again later.";
        }

        ErrorResponse errorResponse = ErrorResponse.builder(exception, HttpStatus.INTERNAL_SERVER_ERROR,
                        exception.getMessage())
                .title(title)
                .detail(detail)
                .property("service", "FeedService")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}