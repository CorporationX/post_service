package faang.school.postservice.exception;

import faang.school.postservice.dto.error.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception){

        ErrorDto errorDto = new ErrorDto();
        errorDto.setId(UUID.randomUUID());
        errorDto.setMessage(exception.getMessage());

        return new ResponseEntity<>("RunTimeException occurred: " +
                errorDto.getMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRunTimeException(
            RuntimeException exception) {
        System.out.println("RunTimeException occurred: " + exception.getMessage());
        return new ResponseEntity<>(
                "RunTime error occurred: " + exception.getMessage(),
                HttpStatus.BAD_REQUEST);
    }
}
