package faang.school.postservice.advice;

import faang.school.postservice.exception.DataValidException;
import faang.school.postservice.exception.DtoGlobalException;
import faang.school.postservice.exception.DtoGlobalExceptionList;
import faang.school.postservice.exception.EntityAlreadyExistException;
import faang.school.postservice.exception.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DtoGlobalExceptionList> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<DtoGlobalException> error = e.getFieldErrors().stream().map(message -> new DtoGlobalException(message.getDefaultMessage())).toList();
        log.error("Data validation exception occurred: {}", e.getAllErrors());
        return new ResponseEntity<>(new DtoGlobalExceptionList(error), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<DtoGlobalExceptionList> constraintViolationException(ConstraintViolationException e) {
        List<DtoGlobalException> error = e.getConstraintViolations().stream().map(message -> new DtoGlobalException(message.getMessage())).toList();
        log.error("Data validation exception occurred: {}", e.toString());
        return new ResponseEntity<>(new DtoGlobalExceptionList(error), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<DtoGlobalException> entityNotFoundException(EntityNotFoundException e) {
        log.error("the object was not found in the database: {}", e.toString());
        return new ResponseEntity<>(new DtoGlobalException(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(EntityAlreadyExistException.class)
    public ResponseEntity<DtoGlobalException> entityAlreadyExistException(EntityAlreadyExistException e) {
        log.error("the object already exist in the database: {}", e.toString());
        return new ResponseEntity<>(new DtoGlobalException(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<DtoGlobalException> runtimeException(RuntimeException e) {
        log.error("RuntimeException: {}", e.getMessage());
        return new ResponseEntity<>(new DtoGlobalException(e.getMessage()), HttpStatusCode.valueOf(500));
    }

}
