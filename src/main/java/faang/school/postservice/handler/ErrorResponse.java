package faang.school.postservice.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
class ErrorResponse {
    private String message;
    private List<String> details;
}
