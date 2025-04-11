package faang.school.postservice.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        int statusCode,
        LocalDateTime timestamp
) {
}
