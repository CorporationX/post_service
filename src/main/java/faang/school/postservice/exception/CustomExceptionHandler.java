package faang.school.postservice.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.charset.StandardCharsets;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<String> dataNotFound(DataNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), getHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IntegrationException.class)
    public ResponseEntity<String> integrationException(IntegrationException e) {
        return new ResponseEntity<>(e.getMessage(), getHeaders(), HttpStatus.BAD_REQUEST);
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = MediaType.TEXT_PLAIN;
        headers.setContentType(new MediaType(mediaType.getType(), mediaType.getSubtype(), StandardCharsets.UTF_8));
        return headers;
    }
}
