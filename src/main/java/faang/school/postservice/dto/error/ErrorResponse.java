package faang.school.postservice.dto.error;

import lombok.Builder;

@Builder
public record ErrorResponse(
        Integer code,
        String message
) {
}