package faang.school.postservice.exception.dto;

public record ErrorResponseDto(
        int status,
        String message
) {
}
