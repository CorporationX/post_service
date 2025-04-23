package faang.school.postservice.handler;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.MaxUploadCountExceededException;
import faang.school.postservice.exception.ResourceProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class PostControllerExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleDataValidationException(DataValidationException e) {
        String message = e.getMessage();
        log.error("DataValidationException caught: {}", message);
        return message;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMaxUploadCountExceededException(MaxUploadCountExceededException e) {
        String message = e.getMessage();
        log.error("Count of images is exceeded: {}", message);
        return message;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleResourceProcessingException(ResourceProcessingException e) {
        String message = e.getMessage();
        log.error("The resource can't be formed: {}", message);
        return message;
    }
}
