package faang.school.postservice.util.exceptionhandler;

import faang.school.postservice.dto.response.ErrorResponse;
import faang.school.postservice.util.exception.CreatePostException;
import faang.school.postservice.util.exception.DataValidationException;
import faang.school.postservice.util.exception.DeletePostException;
import faang.school.postservice.util.exception.PublishPostException;
import faang.school.postservice.util.exception.UpdatePostException;
import feign.FeignException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CreatePostException.class)
    public ResponseEntity<ErrorResponse> handleException(CreatePostException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleException() {
        return ResponseEntity.badRequest().body(new ErrorResponse("Some error with Feign has been occurred", LocalDateTime.now()));
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<ErrorResponse> handleException(DataValidationException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(PublishPostException.class)
    public ResponseEntity<ErrorResponse> handleException(PublishPostException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(UpdatePostException.class)
    public ResponseEntity<ErrorResponse> handleException(UpdatePostException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(DeletePostException.class)
    public ResponseEntity<ErrorResponse> handleException(DeletePostException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }
}
