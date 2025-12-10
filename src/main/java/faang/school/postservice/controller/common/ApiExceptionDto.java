package faang.school.postservice.controller.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiExceptionDto {
    private String message;
    private String status;
    private long timestamp;
    private ErrorType errorType;

    @AllArgsConstructor
    enum ErrorType {
        BUSINESS_ERROR("R_01", "Business Error"),
        SERVER_ERROR("S_01", "Internal server error");

        private String code;
        private String description;
    }
}
