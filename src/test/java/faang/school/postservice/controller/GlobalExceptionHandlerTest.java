package faang.school.postservice.controller;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ImageProcessingException;
import faang.school.postservice.exception.KafkaPostPublisherException;
import feign.FeignException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private ResponseEntity<String> response;

    @DisplayName("Обработка DataValidationException: должен возвращать статус BAD_REQUEST и сообщение об ошибке валидации")
    @Test
    public void givenDataValidationExceptionWhenGlobalExceptionHandelThenBadRequest() {
        DataValidationException exception = new DataValidationException("Invalid data");

        response = globalExceptionHandler.handleDataValidationException(exception);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Data validation error: Invalid data", response.getBody());
    }

    @DisplayName("Обработка EntityNotFoundException: должен возвращать статус NOT_FOUND и сообщение о ненайденной сущности")
    @Test
    public void givenEntityNotFoundExceptionWhenGlobalExceptionHandelThenNotFound() {
        EntityNotFoundException exception = new EntityNotFoundException("Entity not found");

        response = globalExceptionHandler.handleEntityNotFoundException(exception);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals("Entity not found: Entity not found", response.getBody());
    }

    @DisplayName("Обработка общего Exception: должен возвращать статус INTERNAL_SERVER_ERROR и сообщение о внутренней ошибке")
    @Test
    public void givenExceptionWhenGlobalExceptionHandelThenInternalServerError() {
        Exception exception = new Exception("Internal server error");

        response = globalExceptionHandler.handleException(exception);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertEquals("An internal error has occurred: Internal server error", response.getBody());
    }

    @DisplayName("Обработка MethodArgumentNotValidException: должен возвращать статус BAD_REQUEST и map с ошибками валидации")
    @Test
    public void givenMethodArgumentNotValidExceptionWhenGlobalExceptionHandelThenBadRequest() {
        MethodArgumentNotValidException exception = Mockito.mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = Mockito.mock(BindingResult.class);

        FieldError fieldError = new FieldError("objectName", "fieldName", "defaultMessage");
        Mockito.when(exception.getBindingResult())
                .thenReturn(bindingResult);
        Mockito.when(bindingResult.getFieldErrors())
                .thenReturn(Collections.singletonList(fieldError));

        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidationExceptions(exception);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals(Collections.singletonMap("fieldName", "defaultMessage"), response.getBody());
    }

    @DisplayName("Обработка NullPointerException: должен возвращать статус BAD_REQUEST и сообщение о незаполненном поле")
    @Test
    void givenNullPointerExceptionWhenHandleNullPointerExceptionThenBadRequest() {
        NullPointerException exception = new NullPointerException("test message");

        response = globalExceptionHandler.handleNullPointerException(exception);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Required field is missing test message", response.getBody());
    }

    @DisplayName("Обработка ImageProcessingException: должен возвращать статус UNPROCESSABLE_ENTITY и сообщение об ошибке")
    @Test
    void givenImageProcessingExceptionWhenHandleImageProcessingExceptionThenUnprocessableEntity() {
        ImageProcessingException exception = new ImageProcessingException("Image error");

        response = globalExceptionHandler.handleImageProcessingException(exception);

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        Assertions.assertEquals("Image processing error: Image error", response.getBody());
    }

    @DisplayName("Обработка FeignException: должен возвращать оригинальный статус и сообщение об ошибке")
    @Test
    void givenFeignExceptionWhenHandleFeignExceptionThenOriginalStatus() {
        FeignException exception = Mockito.mock(FeignException.class);
        Mockito.when(exception.status()).thenReturn(404);
        Mockito.when(exception.getMessage()).thenReturn("Not found");

        response = globalExceptionHandler.handleFeignException(exception);

        Assertions.assertEquals(404, response.getStatusCode().value());
        Assertions.assertEquals("External service error: Not found", response.getBody());
    }


    @DisplayName("Обработка NullPointerException: должен возвращать статус BAD_REQUEST и сообщение о незаполненном поле")
    @Test
    void givenKafkaPostPublisherExceptionWhenGlobalExceptionHandelThenInternalServerError() {
        KafkaPostPublisherException exception = new KafkaPostPublisherException("test message", new Throwable("test"));

        response = globalExceptionHandler.handleKafkaPostPublisherException(exception);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertEquals("Kafka post publisher error: test message", response.getBody());
    }

}