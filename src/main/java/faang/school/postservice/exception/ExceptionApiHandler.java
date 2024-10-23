package faang.school.postservice.exception;

import com.amazonaws.SdkBaseException;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import faang.school.postservice.exception.model.ApiError;
import faang.school.postservice.service.S3.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Target;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestControllerAdvice
public class ExceptionApiHandler {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @ExceptionHandler({AmazonS3Exception.class, SdkBaseException.class})
    @Target(S3Service.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void amazonS3Exception(SdkBaseException exception) {
        log.debug(exception.getMessage());
    }




    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception exception) {
        log.debug(exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("INTERNAL_SERVER_ERROR", "An unexpected error occurred.",
                        exception.getMessage(), LocalDateTime.now().format(DATE_FORMAT)));
    }
}
