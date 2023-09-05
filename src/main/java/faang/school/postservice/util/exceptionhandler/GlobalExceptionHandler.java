package faang.school.postservice.util.exceptionhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.postservice.dto.response.ErrorResponse;
import faang.school.postservice.util.exception.EntityNotFoundException;
import faang.school.postservice.util.exception.NotAllowedException;
import faang.school.postservice.util.exception.CreatePostException;
import faang.school.postservice.util.exception.DataValidationException;
import faang.school.postservice.util.exception.DeletePostException;
import faang.school.postservice.util.exception.EntitySchedulingException;
import faang.school.postservice.util.exception.GetPostException;
import faang.school.postservice.util.exception.InvalidKeyException;
import faang.school.postservice.util.exception.NotFoundException;
import faang.school.postservice.util.exception.PostNotFoundException;
import faang.school.postservice.util.exception.PublishPostException;
import faang.school.postservice.util.exception.UpdatePostException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CreatePostException.class)
    public ResponseEntity<ErrorResponse> handleException(CreatePostException e) {
        log.error("Error has been occurred when creating new post: {}", e.getMessage(), e);

        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleException(FeignException e) {
        log.error("Error with Feign has been occurred: {}", e.getMessage(), e);

        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleException(MethodArgumentNotValidException e) {
        var bindingResult = e.getBindingResult();
        Map<String, String> fieldErrors = new HashMap<>();

        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> {
                fieldErrors.put(error.getField(), error.getDefaultMessage());
            });
        }
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(fieldErrors);
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<ErrorResponse> handleException(DataValidationException e) {
        log.error("Error has been occurred when validating data: {}", e.getMessage(), e);

        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(PublishPostException.class)
    public ResponseEntity<ErrorResponse> handleException(PublishPostException e) {
        log.error("Error has been occurred when publishing post: {}", e.getMessage(), e);

        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(PostNotFoundException e) {
        log.error("Error has been occurred when finding post: {}", e.getMessage(), e);

        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(UpdatePostException.class)
    public ResponseEntity<ErrorResponse> handleException(UpdatePostException e) {
        log.error("Error has been occurred when updating post: {}", e.getMessage(), e);

        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(DeletePostException.class)
    public ResponseEntity<ErrorResponse> handleException(DeletePostException e) {
        log.error("Error has been occurred when deleting post: {}", e.getMessage(), e);

        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(EntityNotFoundException e) {
        log.error("Error has been occurred when deleting post: {}", e.getMessage(), e);

        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(GetPostException.class)
    public ResponseEntity<ErrorResponse> handleException(GetPostException e) {
        log.error("Error has been occurred when getting post: {}", e.getMessage(), e);

        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidKeyException.class)
    public ResponseEntity<ErrorResponse> handleException(InvalidKeyException e) {
        log.error("Error has been occurred when keys for task: {}", e.getMessage(), e);

        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleException(HttpMessageNotReadableException e) {
        log.error("Error has been occurred when parsing dto: {}", e.getMessage(), e);

        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(EntitySchedulingException.class)
    public ResponseEntity<ErrorResponse> handleException(EntitySchedulingException e) {
        log.error("Error has been occurred when scheduling task: {}", e.getMessage(), e);

        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<String> handleJsonProcessingException(JsonProcessingException ex) {
        log.error(ex.getMessage(), ex.getCause());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(NotAllowedException.class)
    public ResponseEntity<String> handleNotAllowedException(NotAllowedException ex) {
        log.error(ex.getMessage(), ex.getCause());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public faang.school.postservice.util.exceptionhandler.ErrorResponse handleNotFoundException(NotFoundException ex) {
        log.error("Not found exception occurred.", ex);
        return new faang.school.postservice.util.exceptionhandler.ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        log.error(ex.getMessage(), ex.getCause());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
