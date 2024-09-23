package faang.school.postservice.controller.post;

import faang.school.postservice.exception.ErrorResponse;
import faang.school.postservice.exception.redis.RedisTransactionInterrupted;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class RedisExceptionHandler {

    @ExceptionHandler(RedisTransactionInterrupted.class)
    public ResponseEntity<ErrorResponse> handleRedisTransactionInterrupted(RedisTransactionInterrupted exception) {
        log.error(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .message(exception.getMessage())
                        .build());
    }
}
