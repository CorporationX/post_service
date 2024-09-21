package faang.school.postservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(InputValidationException.class)
    public ResponseEntity<?> handleInputValidationErrors(InputValidationException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(ex.getMap()), HttpStatus.BAD_REQUEST
        );
    }
}
