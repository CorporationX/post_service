package faang.school.postservice.exception;

import lombok.Data;

import java.util.Map;

@Data
public class ErrorResponse {
    private Map<String, String> errorFields;
    public ErrorResponse(Map<String, String> map) {
        this.errorFields = map;
    }
}
