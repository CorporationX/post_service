package faang.school.postservice.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * ErrorResponse — описание класса.
 * <p>
 * Класс для возврата из обработчиков ошибок
 * </p>
 *
 * @author agent
 * @since 05.07.2025
 */
@Getter
public class ErrorResponse {
    private final int status;
    private final String message;
    private final String timestamp;

    @JsonCreator
    public ErrorResponse(
            @JsonProperty("status") int status,
            @JsonProperty("message") String message,
            @JsonProperty("timestamp") String timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }
}