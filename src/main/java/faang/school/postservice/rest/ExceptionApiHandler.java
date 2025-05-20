package faang.school.postservice.rest;

import faang.school.postservice.dto.ErrorResponseDto;
import faang.school.postservice.exception.LikeExistsException;
import faang.school.postservice.exception.LikeNotFoundException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionApiHandler {
    private final Utils utils;

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> mismatchException(MethodArgumentTypeMismatchException exception) {
        log.error("{}", exception.getMessage(), exception);
        return ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(new ErrorResponseDto(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> argumentNotValidException(MethodArgumentNotValidException exception) {
        String errorMessage = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> utils.format("'{}': {}", error.getField(), error.getDefaultMessage()))
            .collect(Collectors.joining("; "));
        log.error("{}", exception.getMessage(), exception);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponseDto(errorMessage));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> userNotFoundException(UserNotFoundException exception) {
        //todo: преобразовать строку в json. достать значение атрибута errorMessage
        // и создать из него объект ErrorResponseDto
        log.error("{}", exception.getMessage(), exception);
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(exception.getMessage());
    }

    @ExceptionHandler(LikeNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> likeNotFoundException(LikeNotFoundException exception) {
        log.error("{}", exception.getMessage(), exception);
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponseDto(exception.getMessage()));
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> postNotFoundException(PostNotFoundException exception) {
        log.error("{}", exception.getMessage(), exception);
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponseDto(exception.getMessage()));
    }

    @ExceptionHandler(LikeExistsException.class)
    public ResponseEntity<ErrorResponseDto> likeExistsException(LikeExistsException exception) {
        log.error("{}", exception.getMessage(), exception);
        return ResponseEntity
            .status(HttpStatus.TOO_MANY_REQUESTS)
            .body(new ErrorResponseDto(exception.getMessage()));
    }

}
