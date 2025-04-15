package faang.school.postservice.exception;

import lombok.Builder;

import java.time.Instant;

@Builder
public class ErrorResponse {
    private String message;
    private String origin;
    private String errorCode;
    private Instant timestamp;
}