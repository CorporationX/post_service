package faang.school.postservice.controller;

import faang.school.postservice.exception.*;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для контроллеров.
 * Этот класс перехватывает исключения, возникающие в контроллерах, и возвращает соответствующие HTTP-ответы.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключения типа {@link DataValidationException}.
     * Возвращает HTTP-ответ со статусом 400 (Bad Request) и сообщением об ошибке.
     *
     * @param dataValidationException исключение, которое было выброшено
     * @return ResponseEntity с сообщением об ошибке и статусом 400
     */
    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<String> handleDataValidationException(DataValidationException dataValidationException) {
        log.error("Data validation error: {}", dataValidationException.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(String.format("Data validation error: %s", dataValidationException.getMessage()));
    }

    /**
     * Обрабатывает исключения типа {@link EntityNotFoundException}.
     * Возвращает HTTP-ответ со статусом 404 (Not Found) и сообщением об ошибке.
     *
     * @param entityNotFoundException исключение, которое было выброшено
     * @return ResponseEntity с сообщением об ошибке и статусом 404
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException entityNotFoundException) {
        log.error("Entity not found: {}", entityNotFoundException.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(String.format("Entity not found: %s", entityNotFoundException.getMessage()));
    }

    /**
     * Обрабатывает исключения типа {@link ImageProcessingException} (ошибки обработки изображений).
     * Возникают при проблемах с чтением, изменением размера или сохранением изображений.
     *
     * @param imageProcessingException перехваченное исключение, содержащее информацию об ошибке
     * @return ответ с сообщением об ошибке (HTTP 422 Unprocessable Entity)
     */
    @ExceptionHandler(ImageProcessingException.class)
    public ResponseEntity<String> handleImageProcessingException(ImageProcessingException imageProcessingException) {
        log.error("Image processing failed: {}", imageProcessingException.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body("Image processing error: " + imageProcessingException.getMessage());
    }

    /**
     * Обрабатывает исключение {@link MethodArgumentNotValidException}, которое возникает при валидации данных,
     * переданных в метод контроллера. Этот метод собирает все ошибки валидации и возвращает их в виде
     * JSON-объекта, где ключ — это имя поля, а значение — сообщение об ошибке.
     *
     * @param exception Исключение {@link MethodArgumentNotValidException}, содержащее информацию об ошибках валидации.
     * @return Объект {@link ResponseEntity}, содержащий:
     *         <ul>
     *             <li>Тело ответа в виде {@link Map}, где ключ — это имя поля, а значение — сообщение об ошибке.</li>
     *             <li>HTTP-статус {@link HttpStatus#BAD_REQUEST} (400), указывающий на некорректный запрос
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        log.error("Validation exception: {}", exception.getMessage());

        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Обрабатывает NullPointerException, включая ошибки валидации @NotNull.
     * <p>
     * Логирует предупреждение и возвращает ответ со статусом 400 (Bad Request).
     *
     * @param exception исключение типа NullPointerException
     * @return ResponseEntity с HTTP-статусом 400 и сообщением об ошибке,
     * указывающим на отсутствующее обязательное поле
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleNullPointerException(NullPointerException exception) {
        log.error("Null pointer: {}", exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(String.format("Required field is missing %s", exception.getMessage()));
    }

    /**
     * Обрабатывает исключения FeignClient при взаимодействии с внешними сервисами.
     * <p>
     * Пробрасывает оригинальный HTTP-статус из исключения и возвращает сообщение
     * об ошибке внешнего сервиса.
     *
     * @param exception исключение типа FeignException
     * @return ResponseEntity с оригинальным HTTP-статусом из исключения
     * и сообщением об ошибке внешнего сервиса
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<String> handleFeignException(FeignException exception) {
        log.error("Feign exception: {}", exception.getMessage(), exception);

        return ResponseEntity.status(exception.status())
                .body(String.format("External service error: %s", exception.getMessage()));
    }

    /**
     * Обрабатывает исключения PostToKafkaSender при взаимодействии с Kafka.
     * <p>
     * Логирует предупреждение и возвращает ответ со статусом 400 (Bad Request).
     *
     * @param exception исключение которое было выброшено
     * @return ResponseEntity с HTTP-статусом 500 и сообщением об ошибке
     */
    @ExceptionHandler(KafkaPostPublisherException.class)
    public ResponseEntity<String> handleKafkaPostPublisherException(KafkaPostPublisherException exception) {
        log.error("Kafka exception: {}", exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(String.format("Kafka post publisher error: %s", exception.getMessage()));
    }

    /**
     * Обрабатывает все остальные исключения, которые не были перехвачены другими обработчиками.
     * Возвращает HTTP-ответ со статусом 500 (Internal Server Error) и сообщением об ошибке.
     *
     * @param exception исключение, которое было выброшено
     * @return ResponseEntity с сообщением об ошибке и статусом 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        log.error("Internal server error: {}", exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(String.format("An internal error has occurred: %s", exception.getMessage()));
    }
}