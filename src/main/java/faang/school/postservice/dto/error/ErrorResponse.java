package faang.school.postservice.dto.error;

public record ErrorResponse(
        String error,
        String message
) {
}
