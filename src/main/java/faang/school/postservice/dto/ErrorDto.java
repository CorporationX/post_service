package faang.school.postservice.dto;

import lombok.Builder;

@Builder
public record ErrorDto(
        String error,
        String message
)
{  }
