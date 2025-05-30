package faang.school.postservice.controller.handler;

import lombok.Builder;

@Builder
public record ErrorResponse(

        String timestamp,
        int status,
        String error,
        String message
) {}
