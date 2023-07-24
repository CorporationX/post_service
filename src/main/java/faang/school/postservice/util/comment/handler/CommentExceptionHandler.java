package faang.school.postservice.util.comment.handler;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.NotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class CommentExceptionHandler {
    @ExceptionHandler(DataValidationException.class)
    public String handleDataValidationException(DataValidationException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(NotFoundException.class)
    public String handleNotFoundException(NotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex) {
        return ex.getClass() + " " + ex.getMessage();
    }
}
