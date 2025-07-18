package faang.school.postservice.dto.error;

import java.util.List;

public record ValidationErrorResponse(
        String error,
        String message,
        List<ValidationErrorDetail> details
) {
}