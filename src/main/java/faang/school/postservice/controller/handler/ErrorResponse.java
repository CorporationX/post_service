package faang.school.postservice.controller.handler;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    private String error;
    private int status;
}