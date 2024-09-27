package faang.school.postservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(assignableTypes = {
      AlbumController.class
})
public class ControllerAdvice {
    @ExceptionHandler({
          Exception.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validationNotPassedHandler(Exception exception) {
        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                exception.getClass().getSimpleName(),
                exception.getMessage()
        );
    }
}
