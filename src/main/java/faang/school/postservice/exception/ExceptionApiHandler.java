package faang.school.postservice.exception;

import com.amazonaws.SdkBaseException;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import faang.school.postservice.exception.model.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestControllerAdvice
public class ExceptionApiHandler {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler({AmazonS3Exception.class, SdkBaseException.class})
    public ResponseEntity<ApiError> amazonS3Exception(SdkBaseException exception) {
        log.debug(exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("INTERNAL_SERVER_ERROR", "Something has gone wrong AWS SDK.",
                      exception.getMessage(), LocalDateTime.now().format(DATE_FORMAT)));
    }

    @ExceptionHandler({InterruptedException.class , ExecutionException.class})
    public ResponseEntity<ApiError> fileS3Exception(FileS3Exception exception) {
        log.debug(exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("INTERNAL_SERVER_ERROR", "One of the thread ended with exception.",
                        exception.getMessage(), LocalDateTime.now().format(DATE_FORMAT)));
    }
}
