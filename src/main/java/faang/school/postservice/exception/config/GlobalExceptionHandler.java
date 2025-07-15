package faang.school.postservice.exception.config;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.FileCorruptedException;
import faang.school.postservice.exception.ImageProcessingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Map<Class<? extends Throwable>, HttpStatus> exceptionStatusMap = new HashMap<>();

    static {
        exceptionStatusMap.put(EntityNotFoundException.class, HttpStatus.NOT_FOUND);
        exceptionStatusMap.put(MethodArgumentNotValidException.class, HttpStatus.BAD_REQUEST);
        exceptionStatusMap.put(HttpMessageNotReadableException.class, HttpStatus.BAD_REQUEST);
        exceptionStatusMap.put(DataValidationException.class, HttpStatus.BAD_REQUEST);
        exceptionStatusMap.put(FileCorruptedException.class, HttpStatus.BAD_REQUEST);
        exceptionStatusMap.put(ImageProcessingException.class, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        HttpStatus status = findHttpStatus(ex.getClass());
        ErrorResponse errorResponse = new ErrorResponse(status.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, status);
    }

    private HttpStatus findHttpStatus(Class<?> exceptionClass) {
        while (exceptionClass != null) {
            HttpStatus status = exceptionStatusMap.get(exceptionClass);
            if (status != null) {
                return status;
            }
            exceptionClass = exceptionClass.getSuperclass();
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
