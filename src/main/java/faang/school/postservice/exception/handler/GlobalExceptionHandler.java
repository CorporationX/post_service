package faang.school.postservice.exception.handler;

import faang.school.postservice.exception.InvalidPostAuthorsException;
import faang.school.postservice.exception.LanguageToolException;
import faang.school.postservice.exception.PostNotFoundException;
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
}