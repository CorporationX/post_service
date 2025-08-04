package faang.school.postservice.exception;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

/**
 * GlobalExceptionHandler — централизованный обработчик исключений для REST-контроллеров.
 *
 * <p>Обрабатывает различные исключения, возникающие во время выполнения запросов, и возвращает
 * клиенту стандартизированные ответы с информацией об ошибках в формате JSON.
 * Это помогает унифицировать обработку ошибок и улучшить качество взаимодействия API с клиентами.</p>
 *
 * <p>В данном классе реализованы обработчики для:
 * <ul>
 *     <li>Некорректного формата JSON в запросах (HttpMessageNotReadableException)</li>
 *     <li>Ошибок валидации данных (@Valid) (MethodArgumentNotValidException)</li>
 *     <li>Случаев, когда ресурс не найден (EntityNotFoundException)</li>
 *     <li>Доступ запрещён (ForbiddenException)</li>
 *     <li>Ошибок валидации данных на уровне бизнес-логики (DataValidationException)</li>
 *     <li>Неавторизованного доступа (UnauthorizedException)</li>
 *     <li>Попыток повторного приобретения премиум-подписки (PremiumAlreadyExistsException)</li>
 *     <li>Неуспешных попыток оплаты (PaymentFailedException)</li>
 *     <li>И любых других необработанных исключений (Exception)</li>
 * </ul>
 * </p>
 *
 * <p>Каждый обработчик возвращает объект ErrorResponse с HTTP-статусом, сообщением и отметкой времени.</p>
 *
 * @author agent
 * @since 05.07.2025
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                      WebRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid JSON format",
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(field -> field.getField() + ": " + field.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                errorMessage,
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler({DataValidationException.class, BadRequestException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(),
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleInternalServerError(InternalServerErrorException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        log.error("@ExceptionHandler(Exception.class):\n", ex);
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}