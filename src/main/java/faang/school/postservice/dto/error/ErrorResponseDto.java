package faang.school.postservice.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class ErrorResponseDto {
    private final String error;
    private final String message;
    private final int status;
    private final String path;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<FieldError> errors;

    @Getter
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
    }
}
