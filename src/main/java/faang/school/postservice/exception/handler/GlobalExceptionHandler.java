package faang.school.postservice.exception.handler;

import faang.school.postservice.exception.AuthorNotFoundException;
import faang.school.postservice.exception.CommentIdMismatchException;
import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.FileProcessException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.exception.KafkaPublishPostException;
import faang.school.postservice.exception.LikeAlreadyExistException;
import faang.school.postservice.exception.LikeNotFoundException;
import faang.school.postservice.exception.PostDtoValidationException;
import faang.school.postservice.exception.PostIdMismatchException;
import faang.school.postservice.exception.PostNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(Exception e) {
        log.error("Exception handled: {}", e.getClass().getSimpleName(), e);
        return buildResponse(e);
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<String> handlePostNotFoundException(PostNotFoundException e) {
        log.warn("Post find error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(PostIdMismatchException.class)
    public ResponseEntity<String> handlePostIdMismatchException(PostIdMismatchException e) {
        log.warn("Post validation error: {}", e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(PostDtoValidationException.class)
    public ResponseEntity<String> handleValidationException(PostDtoValidationException e) {
        log.warn("Post dto validation exception: {}", e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(AuthorNotFoundException.class)
    public ResponseEntity<String> handleAuthorNotFoundException(AuthorNotFoundException e) {
        log.warn("Author error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<String> handleCommentNotFoundException(CommentNotFoundException e) {
        log.warn("Comment find error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(CommentIdMismatchException.class)
    public ResponseEntity<String> handleCommentIdMismatchException(CommentIdMismatchException e) {
        log.warn("Validation comment error: {}", e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());

    }

    @ExceptionHandler(LikeAlreadyExistException.class)
    public ResponseEntity<String> handleLikeAlreadyExistException(LikeAlreadyExistException e) {
        log.warn("Like conflict error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

    }

    @ExceptionHandler(LikeNotFoundException.class)
    public ResponseEntity<String> handleLikeNotFoundException(LikeNotFoundException e) {
        log.warn("Find like error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenException(ForbiddenException e) {
        log.error("ForbiddenException", e);
        return buildResponse(e);
    }

    @ExceptionHandler(FileProcessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleFileProcessException(FileProcessException e) {
        log.error("FileProcessException", e);
        return buildResponse(e);
    }

    @ExceptionHandler(KafkaPublishPostException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleKafkaPublishPostException(KafkaPublishPostException e) {
        log.error("KafkaPublishPostException", e);
        return buildResponse(e);
    }

    private ErrorResponse buildResponse(Exception e) {
        log.error(e.getClass().getSimpleName(), e);
        return ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .error(e.getClass().getName())
                .message(e.getMessage())
                .build();
    }
}