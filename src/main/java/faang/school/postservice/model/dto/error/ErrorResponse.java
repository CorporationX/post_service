package faang.school.postservice.model.dto.error;

import lombok.Builder;

@Builder
public record ErrorResponse(
        Integer code,
        String message
) {
}