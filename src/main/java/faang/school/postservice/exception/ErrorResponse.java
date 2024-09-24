package faang.school.postservice.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private Map<String, String> errorFields;
    private Integer status;
    private String message;
}
