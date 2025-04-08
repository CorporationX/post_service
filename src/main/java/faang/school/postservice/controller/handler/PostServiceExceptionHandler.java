package faang.school.postservice.controller.handler;

import faang.school.postservice.exception.FileValidationException;
import faang.school.postservice.exception.S3Exception;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class PostServiceExceptionHandler {

    @Value("${spring.multipart.max-request-size}")
    private long maxRequestSize;

    @ExceptionHandler(FileValidationException.class)
    public ResponseEntity<String> handleFileValidationException(FileValidationException e) {
        String message = e.getMessage();

        log.error("FileException caught: {}", message);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PostNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handlePostNotFoundException(PostNotFoundException e) {
        log.error("PostNotFoundException caught: ", e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(S3Exception.class)
    public ResponseEntity<String> handleS3Exception(S3Exception e) {
        String message = e.getMessage();

        log.error("S3Exception caught: {}", message);
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
   }

    @ExceptionHandler(TagNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleTagNotFoundException(TagNotFoundException e) {
        log.error("TagNotFoundException caught: ", e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SizeLimitExceededException.class)
    public ResponseEntity<String> handleSizeLimitExceededException(SizeLimitExceededException e) {
        String message = e.getMessage();

        log.error("SizeLimitExceededException caught: {}", message);
        return new ResponseEntity<>("Request size must be less then: " + maxRequestSize + " Mb",
                HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        log.error("RuntimeException caught: ", e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
