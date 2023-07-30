package faang.school.postservice.util.exceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class ErrorResponse {
    private String message;
    private Class<?> classException;
    private LocalDateTime time;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public ErrorResponse(String message, Class<?> classException) {
        this.message = message;
        this.classException = classException;
    }
}
