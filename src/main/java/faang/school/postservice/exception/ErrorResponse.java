package faang.school.postservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Класс для возврата ошибки API
 *
 * @author Linempy
 * @since 27.07.2025
 */
@Getter
@RequiredArgsConstructor
public class ErrorResponse {
    private final String message;
}