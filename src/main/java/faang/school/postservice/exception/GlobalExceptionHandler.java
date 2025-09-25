package faang.school.postservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * GlobalExceptionHandler — централизованный обработчик ошибок для REST-контроллеров
 * <p>Класс перехватывает исключения, возникающие в процессе работы контроллеров,
 * и преобразует их в стандартизированные HTTP-ответы с соответствующими кодами состояния.
 * </p>
 *
 * <p>Обработчик поддерживает следующие типы исключений:</p>
 * <ul>
 *   <li>{@link EntityNotFoundException} - возвращает HTTP 404 (Not Found)</li>
 *   <li>{@link DataValidationException} - возвращает HTTP 400 (Bad Request)</li>
 *   <li>{@link ForbiddenException} - возвращает HTTP 403 (Forbidden)</li>
 *   <li>{@link RuntimeException} - возвращает HTTP 500 (Internal Server Error)</li>
 * </ul>
 *
 * <p>Все ответы оборачиваются в объект {@link ErrorResponse} с сообщением об ошибке.</p>
 *
 * @see ErrorResponse
 * @see EntityNotFoundException
 * @see DataValidationException
 * @see ForbiddenException
 * @author Linempy
 * @since 27.07.2025
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerEntityNotFoundException(EntityNotFoundException e) {
        log.error("Сущность не найдена", e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerDataValidationException(DataValidationException e) {
        log.error("Ошибка валидации данных", e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handlerForbiddenException(ForbiddenException e) {
        log.error("Доступ запрещен", e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerRuntimeException(RuntimeException e) {
        log.error("Внутренняя ошибка сервера", e);
        return new ErrorResponse(e.getMessage());
    }
}