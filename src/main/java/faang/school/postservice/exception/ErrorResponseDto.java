package faang.school.postservice.exception;

import lombok.Builder;

@Builder
public record ErrorResponseDto(
        String errorMessage
) {
}