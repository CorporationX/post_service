package faang.school.postservice.dto.error;

public record ValidationErrorDetail(
        String field,
        String message,
        Object rejectedValue
) {
}
