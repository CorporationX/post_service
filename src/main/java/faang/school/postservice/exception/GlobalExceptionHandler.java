package faang.school.postservice.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.bind.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.MimeTypeUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public void handleValidationException(ValidationException e, HttpServletResponse response) throws IOException {
        log.error("Validation exception", e);
        response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletResponse response) throws IOException {
        log.error("Validation failed for request. Method Argument Not Valid Exception", e);
        Map<String, String> errors = e.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        error -> Objects.requireNonNull(error.getDefaultMessage(), "")
                ));

        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");

        ObjectMapper mapper = new ObjectMapper();
        String jsonResponse = mapper.writeValueAsString(errors);

        response.getWriter().write(jsonResponse);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public void handleNoSuchElementException(NoSuchElementException e, HttpServletResponse response) throws IOException {
        log.error("NoSuchElementException", e);
        response.sendError(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
        log.error("IllegalArgumentException", e);
        response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public void handleIOException(IOException e, HttpServletResponse response) throws IOException {
        log.error("IOException", e);
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }

    @ExceptionHandler(SerialException.class)
    public void handleSerialException(SerialException e, HttpServletResponse response) throws IOException {
        log.error("SerialException", e);
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public void handleRuntimeException(RuntimeException e, HttpServletResponse response) throws IOException {
        log.error("RuntimeException", e);
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public void handleException(Exception e, HttpServletResponse response) throws IOException {
        log.error("Exception", e);
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }
}
